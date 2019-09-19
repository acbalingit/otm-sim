/**
 * Copyright (c) 2018, Gabriel Gomes
 * All rights reserved.
 * This source code is licensed under the standard 3-clause BSD license found
 * in the LICENSE file in the root directory of this source tree.
 */
package output;

import commodity.Path;
import common.Link;
import error.OTMException;
import profiles.Profile1D;
import runner.Scenario;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class PathTravelTimeWriter extends AbstractOutputTimedSubnetwork {

    public boolean instantaneous = true;
    public Path path;
    public Profile1D travel_times;
    public Map<Long, Profile1D> per_link_tt;
    public Map<Long, Double> exited_travel_time;

    public PathTravelTimeWriter(Scenario scenario, String prefix, String output_folder, Long subnetwork_id, Float outDt) throws OTMException {
        super(scenario, prefix, output_folder, null, subnetwork_id, outDt);

        if (subnetwork==null || !subnetwork.is_path)
            throw new OTMException("The requested subnetwork is not a path.");

        this.path = (Path) this.subnetwork;
    }

    public long get_path_id(){
        return path.getId();
    }

    @Override
    public void initialize(Scenario scenario) throws OTMException {
        super.initialize(scenario);
        if(!write_to_file)
            travel_times = new Profile1D(0f,outDt);
            per_link_tt = new HashMap<Long, Profile1D>();
            for (Link link: path.links) {
                per_link_tt.put(link.getId(), new Profile1D(0f,outDt));
            }

            exited_travel_time = new HashMap<Long, Double>();
    }

    @Override
    public String get_output_file() {
        return super.get_output_file() + "_path_tt.txt";
    }

    //////////////////////////////////////////////////////
    // write
    //////////////////////////////////////////////////////

    @Override
    public void write(float timestamp,Object obj) throws OTMException {
        super.write(timestamp,null);

        double travel_time = instantaneous ?
                compute_instantaneous_travel_time() :
                compute_predictive_travel_time();

        if(write_to_file){
            try {
                writer.write(String.format("%f\n",travel_time));
            } catch (IOException e) {
                throw new OTMException(e);
            }
        } else {
            travel_times.add(travel_time);
        }
    }

    public double compute_instantaneous_travel_time(){
        exited_travel_time = path.links.stream().filter(l -> l.is_sink)
                                //  .flatMap(x -> x.link_tt.link_per_veh.keySet().stream())
                                .flatMap(x -> x.link_tt.link_per_veh.entrySet().stream())
                                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));


        for (Link link: path.links) {
            double itt = link.link_tt.instantaneous_travel_time;
            per_link_tt.get(link.getId()).add(itt);
        }

        return path.links.stream().
                mapToDouble(link->link.link_tt.instantaneous_travel_time)
                .sum();
    }

    public double compute_predictive_travel_time(){
        return Double.NaN;
    }

//    public double compute_predictive_travel_time(float start_time){
//        float curr_time = start_time;
//        for(Link link:path.ordered_links)
//            curr_time += link.link_tt.get_value_for_time(curr_time);
//        return (double) (curr_time-start_time);
//    }

//    private double get_value_for_link(Link link){
//
//
//        // TODO FIX THIS
//        return Double.NaN;
//
//        switch(info.model_type){
//
//            case pq:
//            case micro:
//                return info.get_mean_and_clear();
////                return info.has_samples() ? info.get_mean_and_clear() : link.model.get_ff_travel_time();
//
//            case ctm:
//            case mn:
//                return link.get_current_average_travel_time();
//
//            case none:
//                return Double.NaN;
//
//            default:
//                System.err.println("NOT IMPLEMENTED.");
//                return Double.NaN;
//
//        }
//
//    }

    //////////////////////////////////////////////////////
    // read
    //////////////////////////////////////////////////////


//    public double compute_instantaneous_travel_time(float start_time){
//        return Double.NaN;
//
////        return path.ordered_links.stream()
////                .mapToDouble(link->link_tt.get(link.getId()).travel_times.get_value_for_time(start_time))
////                .sum();
//    }


    public List<Double> get_travel_times_sec(){
        return travel_times.get_values();
    }

}
