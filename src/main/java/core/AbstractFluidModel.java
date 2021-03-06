package core;

import commodity.Commodity;
import commodity.Path;
import core.geometry.Side;
import dispatch.Dispatcher;
import error.OTMException;
import jaxb.Lanechanges;
import models.fluid.*;
import models.fluid.nodemodel.NodeModel;
import models.fluid.nodemodel.RoadConnection;
import models.fluid.nodemodel.UpLaneGroup;
import core.packet.PacketLink;
import profiles.Profile1D;
import utils.OTMUtils;
import utils.StochasticProcess;

import java.util.*;

public abstract class AbstractFluidModel extends AbstractModel implements InterfaceFluidModel {

    protected final float max_cell_length;
    public final float dt_sec;
    protected Set<Link> source_links = new HashSet<>();
    protected Set<Link> sink_links = new HashSet<>();
    protected Map<Long, NodeModel> node_models;

    public AbstractFluidModel(String name, Set<Link> links, float dt_sec, StochasticProcess process, jaxb.ModelParams params) throws OTMException {
        super(AbstractModel.Type.Fluid,name,links,process);
        this.dt_sec = dt_sec;
        this.max_cell_length = params.getMaxCellLength()==null ? -1 : params.getMaxCellLength();
    }

    @Override
    public void configure(Scenario scenario, Lanechanges lcs) throws OTMException {
        super.configure(scenario, lcs);

        Set<Node> all_nodes = new HashSet<>();

        // create cells
        for(Link link : links) {

            if(link.is_source())
                source_links.add(link);

            if(link.is_sink())
                sink_links.add(link);

            if(!link.get_start_node().is_source)
                all_nodes.add(link.get_start_node());

            if(!link.get_end_node().is_sink)
                all_nodes.add(link.get_end_node());

            // compute cell length .............
            float r = link.get_full_length()/max_cell_length;
            boolean is_source_or_sink = link.is_source() || link.is_sink();

            int num_cells = is_source_or_sink ?
                    1 :
                    OTMUtils.approximately_equals(r%1.0,0.0) ? (int) r :  1+((int) r);

            float cell_length_meters = link.get_full_length()/num_cells;

            // create cells ....................
            for (AbstractLaneGroup lg : link.get_lgs()) {

                FluidLaneGroup flg = (FluidLaneGroup) lg;
                flg.create_cells(this, cell_length_meters);

                // translate parameters to per-cell units
                set_road_param_apply_cell_length(flg);
            }

            // barriers .........................
            barriers_to_cells(link,link.get_in_barriers(),cell_length_meters,link.get_num_dn_in_lanes());
            barriers_to_cells(link,link.get_out_barriers(),cell_length_meters,link.get_num_dn_in_lanes()+link.get_full_lanes());

        }

        node_models = new HashMap<>();
        for(Node node : all_nodes) {
            NodeModel nm = new NodeModel(node);
            node_models.put(node.getId(),nm);
            nm.build();
        }

    }

    public static void set_road_param_apply_cell_length(FluidLaneGroup flg){
        if(flg.cells==null)
            return;
        float cell_length = flg.length / flg.cells.size() / 1000f;    // [km]
        if (!flg.link.is_source()) {
            flg.nom_ffspeed_cell_per_dt /= cell_length;
            flg.ffspeed_cell_per_dt /= cell_length;
            flg.jam_density_veh_per_cell *= cell_length;
            flg.critical_density_veh *= flg.length/ 1000f;
            flg.wspeed_cell_per_dt /= cell_length;
            flg.compute_lcw();
        }
    }


    //////////////////////////////////////////////////////////////
    // InterfaceModel
    //////////////////////////////////////////////////////////////

    @Override
    public void set_state_for_link(Link link) {
        for(AbstractLaneGroup alg : link.get_lgs()){
            FluidLaneGroup lg = (FluidLaneGroup) alg;
            lg.cells.forEach(x->x.set_state());
        }
    }

    @Override
    public void register_with_dispatcher(Scenario scenario, Dispatcher dispatcher, float start_time){
        dispatcher.register_event(new EventFluidModelUpdate(dispatcher, start_time + dt_sec, this));
        dispatcher.register_event(new EventFluidStateUpdate(dispatcher, start_time + dt_sec, this));
    }

    @Override
    public AbstractLaneGroup create_lane_group(Link link, Side side, Float length, int num_lanes, int start_lane, Set<core.RoadConnection> out_rcs, jaxb.Roadparam rp) throws OTMException {
        return new FluidLaneGroup(link,side,length,num_lanes,start_lane,out_rcs,rp);
    }

    @Override
    public final AbstractDemandGenerator create_source(Link origin, Profile1D profile, Commodity commodity, Path path) {
        return new FluidDemandGenerator(origin,profile,commodity,path);
    }

    @Override
    public void initialize(Scenario scenario, float start_time) throws OTMException {
        super.initialize(scenario,start_time);

        for(NodeModel node_model : node_models.values())
            node_model.initialize(scenario);
    }

    //////////////////////////////////////////////////////////////
    // state equation
    //////////////////////////////////////////////////////////////

    // called by EventFluidModelUpdate
    public final void update_flow(float timestamp) throws OTMException {

        update_flow_I(timestamp);

        // -- MPI communication (in otm-mpi) -- //

        update_flow_II(timestamp);

    }

    // update supplies and demands, then run node model to obtain inter-link flows
    public final void update_flow_I(float timestamp) throws OTMException {

        // lane changes and compute demand and supply
        for(Link link : links)
            compute_lanechange_demand_supply(link,timestamp);

        // compute node inflow and outflow (all nodes except sources)
        node_models.values().forEach(n->n.update_flow(timestamp));

    }

    // compute source and source flows
    // node model exchange packets
    public final void update_flow_II(float timestamp) throws OTMException {

        // add to source links
        for(Link link : source_links){
            for(AbstractLaneGroup alg : link.get_lgs()){
                FluidLaneGroup lg = (FluidLaneGroup)alg;
                lg.cells.get(0).add_vehicles(lg.source_flow,null,null);
            }
        }

        // release from sink links
        for(Link link : sink_links){
            for(AbstractLaneGroup alg : link.get_lgs()) {
                FluidLaneGroup lg = (FluidLaneGroup) alg;
                Map<State,Double> flow_dwn = lg.get_demand();

                lg.release_vehicles(flow_dwn);

                for(Map.Entry<State,Double> e : flow_dwn.entrySet())
                    if(e.getValue()>0)
                        lg.update_flow_accummulators(e.getKey(),e.getValue());
            }
        }

        // node models exchange packets
        for(NodeModel node_model : node_models.values()) {

            // flows on road connections arrive to links on give lanes convert to packets and send
            for(RoadConnection rc : node_model.rcs.values()) {
                Link link = rc.rc.get_end_link();
                link.get_model().add_vehicle_packet(link,timestamp, new PacketLink(rc.f_rs, rc.rc));
            }

            // set exit flows on non-sink lanegroups
            for(UpLaneGroup ulg : node_model.ulgs.values()) {
                ulg.lg.release_vehicles(ulg.f_gs);

                // send lanegroup exit flow to flow accumulator
                for(Map.Entry<State,Double> e : ulg.f_gs.entrySet())
                    if(e.getValue()>0)
                        ulg.lg.update_flow_accummulators(e.getKey(),e.getValue());
            }

        }

    }

    // called by EventFluidStateUpdate
    // intra link flows and states
    protected final void update_fluid_state(float timestamp) throws OTMException {
        for(Link link : links)
            update_link_state(link,timestamp);
    }

    //////////////////////////////////////////////////////////////
    // getters
    //////////////////////////////////////////////////////////////

    public final NodeModel get_node_model_for_node(Long node_id){
        return node_models.get(node_id);
    }

    // PRIVATE

    private static void barriers_to_cells(Link link,Set<Barrier> barriers,float cell_length_meters,int in_lane){

        if(barriers==null || barriers.isEmpty())
            return;

        // inner lane group
        FluidLaneGroup inlg = (FluidLaneGroup) link.get_lgs().stream()
                .filter(lg->lg.start_lane_dn+lg.num_lanes-1==in_lane)
                .findFirst().get();

        // outer full lane
        FluidLaneGroup outlg = (FluidLaneGroup) link.get_lgs().stream()
                .filter(lg->lg.start_lane_dn==in_lane+1)
                .findFirst().get();

        // loop through inner barriers
        for(Barrier b : barriers){

            int start = Math.round((link.get_full_length()-b.start)/cell_length_meters);
            int end = Math.round((link.get_full_length()-b.end)/cell_length_meters);

            if(start>end){

                if(inlg!=null && inlg.cells.size()>=start)
                    for(int i=inlg.cells.size()-start;i<inlg.cells.size()-end;i++)
                        inlg.cells.get(i).out_barrier=true;

                if(outlg!=null && outlg.cells.size()>=start)
                    for(int i=outlg.cells.size()-start;i<outlg.cells.size()-end;i++)
                        outlg.cells.get(i).in_barrier=true;
            }
        }
    }

}
