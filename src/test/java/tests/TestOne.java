/**
 * Copyright (c) 2018, Gabriel Gomes
 * All rights reserved.
 * This source code is licensed under the standard 3-clause BSD license found
 * in the LICENSE file in the root directory of this source tree.
 */
package tests;

import api.API;
import api.info.LinkInfo;
import api.info.ODInfo;
import api.info.Profile1DInfo;
import error.OTMException;
import org.junit.Ignore;
import org.junit.Test;
import output.*;
import runner.OTM;

import java.util.List;

import static org.junit.Assert.fail;

public class TestOne extends AbstractTest {

    @Ignore
    @Test
    public void test_load_for_static_traffic_assignment() {
        try {

            // TODO Add large network to test configurations
            String configfile = "C:\\Users\\gomes\\Dropbox\\gabriel\\work\\beats\\beats_share\\MetroManila_unfiltered.xml";

            API api = OTM.load_for_static_traffic_assignment(configfile);
            
            System.out.println(api.get_node_ids().size());
            System.out.println(api.get_link_connectivity().size());

            LinkInfo link = api.get_link_with_id(107948L);

            System.out.println(link.getFull_length());
            System.out.println(link.get_ffspeed_mps());

            List<ODInfo> odinfo = api.get_od_info();

            Profile1DInfo profile = odinfo.get(0).get_total_demand_vps();

            System.out.println(profile);

        } catch (OTMException e) {
            System.out.print(e);
            fail();
        }
    }

    @Test
    public void run_step_by_step() {
        try {

            float start_time = 0f;
            float duration = 3600f;
            float sim_dt = 2f;

            API api = OTM.load_test("line", sim_dt,true,"ctm");

            api.initialize(start_time);

            float time = start_time;
            float end_time = start_time+duration;
            while(time<end_time){
                api.advance(sim_dt);
                time += sim_dt;
            }

        } catch (OTMException e) {
            System.out.print(e);
            fail();
        }
    }

    @Test
    public void run_one() {
        try {

            float duration = 1000;
            float outdt = 2f;
            float sim_dt = 2f;

            // Load ..............................

            API api = null;
            try {
                api = OTM.load_test("intersection",sim_dt,true,"pq");
            } catch (OTMException e) {
                e.printStackTrace();
            }

            api.set_stochastic_process("deterministic");

            // Output requests .....................
            api.request_links_flow( api.get_link_ids(), outdt);
            api.request_links_veh( api.get_link_ids(), outdt);
//
//            api.request_controller(1L);
//            api.request_actuator(1L);

            // Run .................................
            api.run(0,duration);

            // Print output .........................
            String outfolder = "temp/";
            for(AbstractOutput output :  api.get_output_data()){

                if (output instanceof OutputEventsActuator)
                    ((OutputEventsActuator) output).plot(String.format("%sactuator%d.png",outfolder,((OutputEventsActuator) output).actuator_id));

                if (output instanceof OutputEventsController)
                    ((OutputEventsController) output).plot(String.format("%scontroller%d.png",outfolder,((OutputEventsController) output).controller_id));

                if (output instanceof LinkFlowGlobal)
                    ((LinkFlowGlobal) output).plot_for_links(null,String.format("%sflow.png",outfolder));

                if (output instanceof LinkVehiclesGlobal)
                    ((LinkVehiclesGlobal) output).plot_for_links(null,String.format("%sveh.png",outfolder));

            }

        } catch (OTMException e) {
            System.out.print(e);
            fail();
        }
    }

}
