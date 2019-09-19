package traveltime;

import common.AbstractVehicle;
import common.Link;
import models.AbstractLaneGroup;

import java.util.HashMap;
import java.util.Map;

public class VehicleLaneGroupTimer extends AbstractLaneGroupTimer {

    public int num_samples;
    public double sum_time;
    // public Map<Long, Double> per_veh_time;
    public Map<Long, Double> exit_times = new HashMap<>();

    public Map<Long,Float> entry_time = new HashMap<>();

    public VehicleLaneGroupTimer(AbstractLaneGroup lg,float outDt) {
        super(lg,outDt);
        num_samples = 0;
        sum_time = 0d;
    }

    public void vehicle_enter(float timestamp, AbstractVehicle vehicle){
        entry_time.put(vehicle.getId(),timestamp);
    }

    public void vehicle_exit(float timestamp, AbstractVehicle vehicle, Long link_id, Link next_link){

        // I dont know about this vehicle
        if(!entry_time.containsKey(vehicle.getId()))
            return;

        add_sample(timestamp-entry_time.get(vehicle.getId()));

        // push vehicle travel time in link
        if (lg.link.is_sink) {
            exit_times.put(vehicle.getId(), timestamp - vehicle.start_time);
        }

        entry_time.remove(vehicle);
    }

    public void add_sample(double travel_time_sample){
        num_samples++;
        sum_time += travel_time_sample;
    }

    public boolean has_samples(){
        return num_samples>0;
    }

    @Override
    public double get_mean_and_clear(){
       if (num_samples == 0) return 0;
       double mean = sum_time / ((double) num_samples);
       num_samples = 0;
       sum_time = 0;
       entry_time.clear(); // clear vehicle timer record as well
       return mean;
    }

}
