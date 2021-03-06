package output;

import core.FlowAccumulatorState;
import core.Scenario;
import error.OTMException;
import models.fluid.FluidLaneGroup;
import org.jfree.data.xy.XYSeries;
import profiles.Profile1D;

import java.util.*;

public class OutputCellLanechangeOut extends AbstractOutputTimedCell {

    private Map<Long, List<FlowAccumulatorState>> flw_accs;    // lg id -> list<acc>

    public OutputCellLanechangeOut(Scenario scenario, String prefix, String output_folder, Long commodity_id, Collection<Long> link_ids, Float outDt) throws OTMException {
        super(scenario, prefix, output_folder, commodity_id, link_ids, outDt);
        this.type = Type.cell_lanechange;
    }

    //////////////////////////////////////////////////////
    // InterfaceOutput
    //////////////////////////////////////////////////////

    @Override
    public String get_output_file() {
        return write_to_file ? super.get_output_file() + "_lcout.txt" : null;
    }

    //////////////////////////////////////////////////////
    // AbstractOutput
    //////////////////////////////////////////////////////

    @Override
    public void initialize(Scenario scenario) throws OTMException {
        super.initialize(scenario);
        flw_accs = new HashMap<>();
        for(FluidLaneGroup lg : ordered_lgs)
            flw_accs.put(lg.getId(), lg.request_flow_lcout_accumulators_for_cells(commodity == null ? null : commodity.getId()));
    }

    //////////////////////////////////////////////////////
    // InterfacePlottable
    //////////////////////////////////////////////////////

    @Override
    public String get_yaxis_label() {
        return "lc out";
    }

    @Override
    public void plot(String filename) throws OTMException {
        throw new OTMException("Plot not implemented for Cell output.");
    }

    //////////////////////////////////////////////////////
    // AbstractOutputTimedCell
    //////////////////////////////////////////////////////

    @Override
    protected double[] get_value_for_lanegroup(FluidLaneGroup lg){
        double [] X = new double[lg.cells.size()];
        for(int i=0;i<lg.cells.size();i++)
            X[i] = commodity==null ?
                    flw_accs.get(lg.getId()).get(i).get_total_count() :
                    flw_accs.get(lg.getId()).get(i).get_count_for_commodity(commodity.getId());
        return X;
    }

    @Override
    public List<XYSeries> get_series_for_lg(FluidLaneGroup lg) {

        List<XYSeries> X = new ArrayList<>();
        List<CellProfile> cellprofs = lgprofiles.get(lg.getId());
        for(int i=0;i<cellprofs.size();i++){
            String label = String.format("%d (%d-%d) cell %d",lg.get_link().getId(),lg.get_start_lane_dn(),lg.get_start_lane_dn()+lg.get_num_lanes()-1,i);
            X.add(get_flow_profile_in_vph(cellprofs.get(i)).get_series(label));
        }
        return X;
    }

    //////////////////////////////////////////////////////
    // private
    //////////////////////////////////////////////////////

    private Profile1D get_flow_profile_in_vph(CellProfile cellprof){
        Profile1D profile = cellprof.profile.clone();
        return new Profile1D(profile.start_time,profile.dt,profile.difftimes(3600d/outDt));
    }





}
