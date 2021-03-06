package output;

import core.AbstractLaneGroup;
import core.Link;
import dispatch.Dispatcher;
import error.OTMException;
import cmd.RunParameters;
import core.Scenario;

import java.io.IOException;

public class OutputLaneGroups extends AbstractOutput {

    ///////////////////////////////////////////////////
    // construction
    ///////////////////////////////////////////////////

    public OutputLaneGroups(Scenario scenario, String prefix, String output_folder) throws OTMException {
        super(scenario,prefix,output_folder);
    }

    //////////////////////////////////////////////////////
    // InterfaceOutput
    //////////////////////////////////////////////////////

    @Override
    public String get_output_file() {
        return write_to_file ? super.get_output_file() + "_lanegroups.txt" : null;
    }

    public void write(float timestamp, Object obj) {
        System.err.println("this should not happen");
    }

    @Override
    public void register(RunParameters props, Dispatcher dispatcher) {
        if(writer==null)
            return;
        try {

            for(Link link : scenario.network.links.values())
                for(AbstractLaneGroup lg : link.get_lgs())
                    writer.write(dnlgstring(lg));

            writer.close();
            writer = null;
        } catch (IOException e) {
            return;
        }
    }

    private static String dnlgstring(AbstractLaneGroup lg){
        return String.format("%d\t%d\t%d\t%d\t%d\n",lg.getId() , lg.get_link().getId(),0,lg.get_start_lane_dn(),lg.get_num_lanes());
    }

    private static String uplgstring(AbstractLaneGroup lg){
        return String.format("%d\t%d\t%d\t%d\t%d\n",lg.getId() , lg.get_link().getId(),1,lg.get_start_lane_up(),lg.get_num_lanes());
    }

}
