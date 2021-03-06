package core;

import error.OTMException;
import models.vehicle.spatialq.OutputLinkQueues;
import output.*;

import java.util.*;

import static java.util.stream.Collectors.toSet;

/**
 * Methods for requesting outputs.
 * This class provides methods for requesting certain outputs from the simulation. These methods should be called
 * prior to the initialization of the simulation. Most of them share these parameters:
 * <b>prefix</b> ... If you wish to write the output to files, this string will be used as a prefix for all output files in
 * this run. Otherwise, set it to null. <br>
 * <b>output_folder</b> ... Folder for storing output files. If output should be preserved in memory (for the plot methods),
 * then set to null. <br>
 * <b>commodity_id</b> ... Commodity id for the requested output. <br>
 * <b>link_ids</b> ... Collection of link ids for the requested output. <br>
 * <b>outDt</b> ... Output sampling time in seconds. If the underlying model has a time step (as opposed to an event based
 * model) then outDt should be a multiple of that time step. <br>
 */
public class Output {

    private OTM myapi;

    protected void set_api(OTM myapi){
        this.myapi = myapi;
    }

    // ----------------------------------------------
    // general
    // ----------------------------------------------

    /**
     * Get the set of all output objects.
     * @return Set of output objects
     */
    public Set<AbstractOutput> get_data(){
        Set<AbstractOutput> x = new HashSet<>();
        for(AbstractOutput output : myapi.scenario.outputs)
            if(!output.write_to_file)
                x.add(output);
        return x;
    }

    /**
     * Get the set of all output file names.
     * @return Set of all output file names
     */
    public Set<String> get_file_names(){
        return myapi.scenario.outputs.stream()
                .map(x->x.get_output_file())
                .filter(x->x!=null)
                .collect(toSet());
    }

    /**
     * Clear the outputs.
     */
    public void clear(){
        myapi.scenario.outputs.clear();
    }

    // ----------------------------------------------
    // links
    // ----------------------------------------------

    /**
     * Request link flows
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param commodity_id Id for the requested vehicle type. null means aggregate over commodities.
     * @param link_ids Collection of requested link ids
     * @param outDt Output sampling time in seconds.
     */
    public void request_links_flow(String prefix, String output_folder, Number commodity_id, Collection<? extends Number> link_ids, Float outDt){
        try {
            this.myapi.scenario.outputs.add(new OutputLinkFlow(myapi.scenario,prefix,output_folder,to_long(commodity_id),to_long(link_ids),outDt));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request link vehicles.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param commodity_id Id for the requested vehicle type. null means aggregate over commodities.
     * @param link_ids Collection of requested link ids
     * @param outDt Output sampling time in seconds.
     */
    public void request_links_veh(String prefix,String output_folder,Number commodity_id,Collection<? extends Number> link_ids,Float outDt){
        try {
            this.myapi.scenario.outputs.add(new OutputLinkVehicles(myapi.scenario,prefix,output_folder,to_long(commodity_id),to_long(link_ids),outDt));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request link wsum of vehicles.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param commodity_id Id for the requested vehicle type. null means aggregate over commodities.
     * @param link_ids Collection of requested link ids
     * @param outDt Output sampling time in seconds.
     */
    public void request_links_sum_veh(String prefix,String output_folder,Number commodity_id,Collection<? extends Number> link_ids,Float outDt){
        try {
            this.myapi.scenario.outputs.add(new OutputLinkSumVehicles(myapi.scenario,prefix,output_folder,to_long(commodity_id),to_long(link_ids),outDt));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request vehicles in a mesoscopic queue.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param commodity_id Id for the requested vehicle type. null means aggregate over commodities.
     * @param link_ids Collection of requested link ids
     * @param outDt Output sampling time in seconds.
     */
    public void request_link_queues(String prefix,String output_folder,Number commodity_id, Collection<? extends Number> link_ids, Float outDt){
        try {
            this.myapi.scenario.outputs.add(new OutputLinkQueues(myapi.scenario,prefix,output_folder,to_long(commodity_id),to_long(link_ids),outDt));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------
    // lane groups
    // ----------------------------------------------

    /**
     * Request lane group outputs
     * @param prefix Prefix for the output files.
     * @param output_folder Output folder.
     */
    public void request_lanegroups(String prefix,String output_folder){
        try {
            this.myapi.scenario.outputs.add(new OutputLaneGroups(myapi.scenario,prefix,output_folder));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request lane group flows.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param commodity_id Id for the requested vehicle type. null means aggregate over commodities.
     * @param link_ids Collection of requested link ids
     * @param outDt Output sampling time in seconds.
     */
    public void request_lanegroup_flw(String prefix,String output_folder,Number commodity_id,Collection<? extends Number> link_ids,Float outDt){
        try {
            this.myapi.scenario.outputs.add(new OutputLaneGroupFlow(myapi.scenario,prefix,output_folder,to_long(commodity_id),to_long(link_ids),outDt));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request lane group vehicles.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param commodity_id Id for the requested vehicle type. null means aggregate over commodities.
     * @param link_ids Collection of requested link ids
     * @param outDt Output sampling time in seconds.
     */
    public void request_lanegroup_veh(String prefix,String output_folder,Number commodity_id,Collection<? extends Number> link_ids,Float outDt){
        try {
            this.myapi.scenario.outputs.add(new OutputLaneGroupVehicles(myapi.scenario,prefix,output_folder,to_long(commodity_id),to_long(link_ids),outDt));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request lane group sum vehicles over simulation time steps. Works only for timestep-based models.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param commodity_id Id for the requested vehicle type. null means aggregate over commodities.
     * @param link_ids Collection of requested link ids
     * @param outDt Output sampling time in seconds.
     */
    public void request_lanegroup_sum_veh(String prefix, String output_folder, Number commodity_id, Collection<? extends Number> link_ids, Float outDt){
        try {
            this.myapi.scenario.outputs.add(new OutputLaneGroupSumVehicles(myapi.scenario,prefix,output_folder,to_long(commodity_id),to_long(link_ids),outDt));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    public void request_lanegroup_sum_veh_dwn(String prefix, String output_folder, Number commodity_id, Collection<? extends Number> link_ids, Float outDt){
        try {
            this.myapi.scenario.outputs.add(new OutputLaneGroupSumVehiclesDwn(myapi.scenario,prefix,output_folder,to_long(commodity_id),to_long(link_ids),outDt));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------
    // cells
    // ----------------------------------------------

    /**
     * Request cell flows.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param commodity_id Id for the requested vehicle type. null means aggregate over commodities.
     * @param link_ids Collection of requested link ids
     * @param outDt Output sampling time in seconds.
     */
    public void request_cell_flw(String prefix,String output_folder,Number commodity_id,Collection<? extends Number> link_ids,Float outDt){
        try {
            this.myapi.scenario.outputs.add(new OutputCellFlow(myapi.scenario,prefix,output_folder,to_long(commodity_id),to_long(link_ids),outDt));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request cell vehicles.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param commodity_id Id for the requested vehicle type. null means aggregate over commodities.
     * @param link_ids Collection of requested link ids
     * @param outDt Output sampling time in seconds.
     */
    public void request_cell_veh(String prefix,String output_folder,Number commodity_id,Collection<? extends Number> link_ids,Float outDt){
        try {
            this.myapi.scenario.outputs.add(new OutputCellVehicles(myapi.scenario,prefix,output_folder,to_long(commodity_id),to_long(link_ids),outDt));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request cell sum of vehicles. Works only for timestep-based models.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param commodity_id Id for the requested vehicle type. null means aggregate over commodities.
     * @param link_ids Collection of requested link ids
     * @param outDt Output sampling time in seconds.
     */
    public void request_cell_sum_veh(String prefix, String output_folder, Number commodity_id, Collection<? extends Number> link_ids, Float outDt){
        try {
            this.myapi.scenario.outputs.add(new OutputCellSumVehicles(myapi.scenario,prefix,output_folder,to_long(commodity_id),to_long(link_ids),outDt));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request cell vehicles moving downstream. Works only for timestep-based models.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param commodity_id Id for the requested vehicle type. null means aggregate over commodities.
     * @param link_ids Collection of requested link ids
     * @param outDt Output sampling time in seconds.
     */
    public void request_cell_sum_veh_dwn(String prefix, String output_folder, Number commodity_id, Collection<? extends Number> link_ids, Float outDt){
        try {
            this.myapi.scenario.outputs.add(new OutputCellSumVehiclesDwn(myapi.scenario,prefix,output_folder,to_long(commodity_id),to_long(link_ids),outDt));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request cell vehicles moving to outside lane group. Works only for timestep-based models.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param commodity_id Id for the requested vehicle type. null means aggregate over commodities.
     * @param link_ids Collection of requested link ids
     * @param outDt Output sampling time in seconds.
     */
    public void request_cell_lanechange_out(String prefix, String output_folder, Number commodity_id, Collection<? extends Number> link_ids, Float outDt){
        try {
            this.myapi.scenario.outputs.add(new OutputCellLanechangeOut(myapi.scenario,prefix,output_folder,to_long(commodity_id),to_long(link_ids),outDt));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request cell vehicles moving to incside lane group. Works only for timestep-based models.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param commodity_id Id for the requested vehicle type. null means aggregate over commodities.
     * @param link_ids Collection of requested link ids
     * @param outDt Output sampling time in seconds.
     */
    public void request_cell_lanechange_in(String prefix, String output_folder, Number commodity_id, Collection<? extends Number> link_ids, Float outDt){
        try {
            this.myapi.scenario.outputs.add(new OutputCellLanechangeIn(myapi.scenario,prefix,output_folder,to_long(commodity_id),to_long(link_ids),outDt));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------
    // subnetworks state
    // ----------------------------------------------

    /**
     * Request the travel times on a given path.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param subnetwork_id Id of the requested subnetwork. null means the entire network.
     * @param outDt Output sampling time in seconds.
     */
    public void request_path_travel_time(String prefix,String output_folder,Number subnetwork_id,Float outDt){
        try {
            OutputPathTravelTime path_tt = new OutputPathTravelTime(myapi.scenario,prefix,output_folder,to_long(subnetwork_id),outDt);
            this.myapi.scenario.outputs.add(path_tt);
            this.myapi.scenario.add_path_travel_time(path_tt);
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request VHT for a subnetwork
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param commodity_id Id for the requested vehicle type. null means aggregate over commodities.
     * @param subnetwork_id Id of the requested subnetwork. null means the entire network.
     * @param outDt Output sampling time.
     */
    public void request_subnetwork_vht(String prefix,String output_folder,Number commodity_id,Number subnetwork_id,Float outDt){
        try {
            this.myapi.scenario.outputs.add(new OutputSubnetworkVHT(myapi.scenario,prefix,output_folder,to_long(commodity_id),to_long(subnetwork_id),outDt));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------
    // vehicle events
    // ----------------------------------------------

    /**
     * Request vehicle events.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param commodity_id Id for the requested vehicle type. null means all commodities.
     */
    public void request_vehicle_events(String prefix,String output_folder,Number commodity_id){
        try {
            this.myapi.scenario.outputs.add(new OutputVehicleEvents(myapi.scenario,prefix,output_folder,to_long(commodity_id)));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request vehicle class.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     */
    public void request_vehicle_class(String prefix,String output_folder){
        this.myapi.scenario.outputs.add(new OutputVehicleClass(myapi.scenario,prefix,output_folder));
    }

    /**
     * Request travel times.
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     */
    public void request_travel_time(String prefix, String output_folder){
        this.myapi.scenario.outputs.add(new OutputTravelTime(myapi.scenario,prefix,output_folder));
    }

    // ----------------------------------------------
    // controllers
    // ----------------------------------------------

    /**
     * Request controller events
     * @param prefix Prefix for the output files. null means do not write to file.
     * @param output_folder Output folder. null means do not write to file.
     * @param controller_id Controller id. null is all controllers.
     */
    public void request_controller(String prefix,String output_folder,Number controller_id){
        try {
            this.myapi.scenario.outputs.add(new OutputController(myapi.scenario,prefix,output_folder,to_long(controller_id)));
        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    // ==================================================
    // Integer-based interface for Python connection
    // ==================================================

    private Long to_long(Number x){
        return x==null ? null : Long.valueOf(x.longValue());
    }

    private Collection<Long> to_long(Collection<? extends Number> x){
        if(x==null)
            return null;
        List<Long> X = new ArrayList<>();
        for(Number xx : x)
            X.add(to_long(xx));
        return X;
    }

}
