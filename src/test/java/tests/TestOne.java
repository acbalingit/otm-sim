package tests;

import api.OTMdev;
import api.info.*;
import error.OTMException;
import org.junit.Ignore;
import org.junit.Test;
import output.*;
import runner.OTM;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class TestOne extends AbstractTest {

    @Ignore
    @Test
    public void test_load_for_static_traffic_assignment() {
        try {

            // TODO Add large network to test configurations
            String configfile = "C:\\Users\\gomes\\Dropbox\\gabriel\\work\\beats\\beats_share\\MetroManila_unfiltered.xml";

            api.OTM otm = new api.OTM(configfile,true,true);
            
            System.out.println(otm.scenario.get_node_ids().size());
            System.out.println(otm.scenario.get_link_connectivity().size());

            LinkInfo link = otm.scenario.get_link_with_id(107948L);

            System.out.println(link.getFull_length());

//            Set<ODInfo> odinfo = otm.scenario.get_od_info();

//            Profile1DInfo profile = odinfo.get(0).get_total_demand_vps();

//            System.out.println(profile);

        } catch (OTMException e) {
            System.out.print(e);
            fail();
        }
    }

    @Ignore
    @Test
    public void run_step_by_step() {
        try {

            float start_time = 0f;
            float duration = 3600f;
            float advance_time = 300f;

            String configfile = "C:\\Users\\gomes\\Desktop\\traffic_master\\XML files\\Capstone_0314.xml";
            OTMdev otm = new api.OTMdev(configfile);

            otm.otm.initialize(start_time);

            float time = start_time;
            float end_time = start_time+duration;
            while(time<end_time){
                otm.otm.advance(advance_time);
                System.out.println(otm.otm.get_current_time());
                System.out.println(otm.scenario.network.links.get(0l).get_veh());
                time += advance_time;
            }

        } catch (OTMException e) {
            System.out.print(e);
            fail();
        }
    }

    @Ignore
    @Test
    public void load_one() {
        try {
            String configfile = "/home/gomes/Desktop/test2/scenario_25_nodes.xml";
            api.OTM otm = new api.OTM(configfile,true,false);
            assertNotNull(otm);
        } catch (OTMException e) {
            fail(e.getMessage());
        }
    }

    @Ignore
    @Test
    public void load_for_traffic_assignment() {
        try {

            float sample_dt = 2;
            int num_samp = 10;
            long path_id = 1;

            float start_time = 0.0f;
            float time_horizon = 1000f;

            String configfile = "C:\\Users\\gomes\\Desktop\\seven_links.xml";
            String outfolder  = "C:\\Users\\gomes\\Desktop\\";
            api.OTM otm = new api.OTM(configfile);

//            List<ODInfo> od_infos = otm.scenario.get_od_info();
//            ODInfo od_info = od_infos.get(0);
//            List<SubnetworkInfo> xxx = od_info.get_subnetworks();


            otm.output.request_path_travel_time(path_id, sample_dt);
//            otm.request_links_flow(null, api.get_link_ids(), sample_dt);
//            otm.request_links_veh(null, api.get_link_ids(), sample_dt);


            otm.set_random_seed(1);

            otm.run(start_time,time_horizon);

            boolean instantaneous = true;
            for(AbstractOutput output : otm.output.get_data()){

//                if (output instanceof LinkFlow)
//                    ((LinkFlow) output).plot_for_links(null, String.format("%sflow.png", outfolder));
//
//                if (output instanceof LinkVehicles)
//                    ((LinkVehicles) output).plot_for_links(null, String.format("%sveh.png", outfolder));

//                if(output instanceof PathTravelTimeWriter){
//                    PathTravelTimeWriter ptt = (PathTravelTimeWriter) output;
//                    List<Double> cost_list;
//                    if(instantaneous)
//                        cost_list = ptt.compute_instantaneous_travel_times(start_time, sample_dt, num_samp);
//                    else
//                        cost_list = ptt.compute_predictive_travel_times(start_time, sample_dt, num_samp);
//
//                    System.out.println(cost_list);
//
//                }
            }


        } catch (OTMException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void TempTest() {
        try {
            api.OTM otm = new api.OTM("/home/gomes/Desktop/test/seven_links.xml",true,false);

            long path_id = 1l;
            long comm_id = 1l;
            float start_time = 0f;
            float sample_dt = 10f;
            List<Double> values = new ArrayList<>();
            for(int i=0;i<10;i++)
                values.add(1000d);
            otm.scenario().add_pathfull_demand(path_id, comm_id, start_time,sample_dt, values);


            otm.run(0f,1800f);
        } catch (OTMException e) {
            System.out.print(e);
            fail();
        }
    }

    @Ignore
    @Test
    public void run_one() {
        try {

            String configfile = "/home/gomes/Desktop/test/seven_links.xml";

            float duration = 1800f;
            float outdt = 10f;
            String prefix = "test";
            String output_folder = "temp/";

            // Load ..............................
            api.OTM otm = new api.OTM(configfile,true,false);


            // Output requests .....................
            Set<Long> link_ids = otm.scenario.get_link_ids();
//            otm.output.request_links_flow(prefix,output_folder,null, link_ids, outdt);
//            otm.output.request_links_veh(prefix,output_folder,null, link_ids, outdt);

            otm.output.request_links_flow(null, link_ids, outdt);
            otm.output.request_links_veh(null, link_ids, outdt);

//
//            List<ODInfo> od_infos = api.get_od_info();
//            ODInfo od_info = od_infos.get(0);
//            List<SubnetworkInfo> paths = od_info.get_subnetworks();
//
//            long path_id = paths.get(0).getId();
//
//            api.request_path_travel_time(path_id, outdt);

//            api.request_links_flow(null, api.get_link_ids(), outdt);
//            api.request_links_veh(null, api.get_link_ids(), outdt);

//            api.request_controller(1L);
//            api.request_actuator(1L);

            // Run .................................
            otm.run(0,duration);

            // Print output .........................
            String outfolder = "temp/";
            for(AbstractOutput output :  otm.output.get_data()){

//                if (output instanceof EventsActuator)
//                    ((EventsActuator) output).plot(String.format("%sactuator%d.png",outfolder,((EventsActuator) output).actuator_id));
//
//                if (output instanceof EventsController)
//                    ((EventsController) output).plot(String.format("%scontroller%d.png",outfolder,((EventsController) output).controller_id));

                if (output instanceof LinkFlow)
                    ((LinkFlow) output).plot_for_links(null, String.format("%sflow.png", outfolder));

                if (output instanceof LinkVehicles)
                    ((LinkVehicles) output).plot_for_links(null, String.format("%sveh.png", outfolder));


                if(output instanceof PathTravelTimeWriter){
                    PathTravelTimeWriter ptt = (PathTravelTimeWriter) output;
                    List<Double> travel_times = ptt.get_travel_times_sec();
                    System.out.println(travel_times);
                }

            }

        } catch (OTMException e) {
            System.out.print(e);
            fail();
        }
    }

    @Ignore
    @Test
    public void run_RL() {


//        String configfile = "C:\\Users\\gomes\\Desktop\\traffic_master\\Capstone_0314.xml";
        String configfile = "C:\\Users\\gomes\\Desktop\\traffic_master\\n_Capstone_0426_9000.xml";

        float duration = 3600f;
        float outdt = 300f;

        // Load ..............................
        api.OTM otm = null;

        try {
            otm = new api.OTM(configfile);

//            // Output requests .....................
//            List<Long> list_orig_link_ids = otm.scenario.get_link_ids();
//            otm.output.request_links_flow(null, list_orig_link_ids, outdt);
//            otm.output.request_links_veh(null, list_orig_link_ids, outdt);
//
//            List<Long> ramp_ids = new ArrayList<>();
//            for(ActuatorInfo act_info : otm.scenario.get_actuators())
//                ramp_ids.add(act_info.target.getId());

//            ControllerCapacity controller = (ControllerCapacity) otm.scenario.get_actual_controller_with_id(1);

            // Qtable loop

            double[] sum_vehicles = new double[6];
            double[] sum_flow = new double[6];

//            for( int i=0 ; i<6 ; i++){
//
//                // update control
//                for(Long ramp_id : ramp_ids) {
//                    float rate_ramp = i*300f;
//                    controller.set_rate_vph_for_actuator(ramp_id, rate_ramp);
//                }
//
//                System.out.println("i=" + i);
//
//                otm.run(0,duration);
//
//                // extract output .........................
//                sum_vehicles[i] = 0d;
//                sum_flow[i] = 0d;
//
//                for(AbstractOutput output :  otm.output.get_data()){
//
//                    if (output instanceof LinkFlow){
//
//                        for(Long link_id : list_orig_link_ids) {
//                            Profile1D profile = ((LinkFlow) output).get_flow_profile_for_link_in_vph(link_id);
//                            List<Double> values = profile.get_values();
//    //                        System.out.println(String.format("LinkFlow: id=%d num_values=%d",link_id,values.size()));
//
//                            sum_flow[i] += values.stream().mapToDouble(x->x).sum();
//                        }
//                    }
//
//
//                    if (output instanceof LinkVehicles) {
//                        for(Long link_id : list_orig_link_ids) {
//                            Profile1D profile = ((LinkVehicles) output).get_profile_for_linkid(link_id);
//                            List<Double> values = profile.get_values();
//    //                        System.out.println(String.format("LinkVehicles: id=%d num_values=%d",link_id,values.size()));
//
//                            sum_vehicles[i] += values.stream().mapToDouble(x->x).sum();
//                        }
//                    }
//
//                }
//
//                System.out.println(i + "\t" + sum_flow[i] + "\t" + sum_vehicles[i]);
//
//                // do some Qtable calculation



//            }
        } catch (OTMException e) {
            e.printStackTrace();
        }

    }

    @Ignore
    @Test
    public void run_one_test() {
        try {
            float duration = 1000f;
            float outdt = 10f;
            String prefix = "test";
            String output_folder = "temp/";

            // Load ..............................
            api.OTM api = null;
//            try {
////                api = OTM.load_test("signal_nopocket",true);
////                api = OTM.load("C:\\Users\\gomes\\vbox_shared\\all_cfgs\\100.xml",true,"ctm");
//            } catch (OTMException e) {
//                e.printStackTrace();
//            }

            // Output requests .....................
            api.output.request_links_flow(prefix,output_folder,null, api.scenario.get_link_ids(), outdt);
            api.output.request_links_veh(prefix,output_folder,null, api.scenario.get_link_ids(), outdt);

//            api.request_links_flow(null, api.get_link_ids(), outdt);
//            api.request_links_veh(null, api.get_link_ids(), outdt);

//            api.request_controller(1L);
//            api.request_actuator(1L);

            // Run .................................
            api.run(0,duration);

            // Print output .........................
            String outfolder = "temp/";
            for(AbstractOutput output :  api.output.get_data()){

                if (output instanceof EventsActuator)
                    ((EventsActuator) output).plot(String.format("%sactuator%d.png",outfolder,((EventsActuator) output).actuator_id));

                if (output instanceof EventsController)
                    ((EventsController) output).plot(String.format("%scontroller%d.png",outfolder,((EventsController) output).controller_id));

                if (output instanceof LinkFlow)
                    ((LinkFlow) output).plot_for_links(null,String.format("%sflow.png",outfolder));

                if (output instanceof LinkVehicles)
                    ((LinkVehicles) output).plot_for_links(null,String.format("%sveh.png",outfolder));

            }

        } catch (OTMException e) {
            System.out.print(e);
            fail();
        }
    }

    @Test
    public void run_main(){

        //    0 command
        //    1 configfile
        //    2 prefix
        //    3 output_request
        //    4 output folder
        //    5 start_time
        //    6 duration

        String resource_folder = (new File("src/test/resources")).getAbsolutePath()+ File.separator ;

        String[] args = new String[7];
        args[0] = "-run";
        args[1] = resource_folder + "line.xml";
        args[2] = "mytest";
        args[3] = resource_folder+"sample_output_request.xml";
        args[4] = resource_folder+"test_output";
        args[5] = "0";
        args[6] = "100";

        OTM.main(args);

    }

}
