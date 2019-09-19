/**
 * Copyright (c) 2018, Gabriel Gomes
 * All rights reserved.
 * This source code is licensed under the standard 3-clause BSD license found
 * in the LICENSE file in the root directory of this source tree.
 */
package traveltime;

import java.util.HashMap;
import java.util.Map;

import common.Link;
import models.AbstractFluidModel;
import models.AbstractLaneGroup;
import models.AbstractVehicleModel;

public class LinkTravelTimer {

    public double instantaneous_travel_time;
    public Map<Long, Double> link_per_veh;
    public Link link;

    public LinkTravelTimer(Link link,float outDt){
        this.link = link;

        // create FluidLaneGroupTimer
        if( link.model instanceof AbstractFluidModel )
            for(AbstractLaneGroup lg : link.lanegroups_flwdn.values())
                lg.travel_timer = new FluidLaneGroupTimer(lg,outDt);

        // create VehicleLaneGroupTimer
        if( link.model instanceof AbstractVehicleModel )
            for(AbstractLaneGroup lg : link.lanegroups_flwdn.values())
                lg.travel_timer = new VehicleLaneGroupTimer(lg,outDt);

        link_per_veh = new HashMap<Long, Double>();
    }

    public void update_travel_time(){
        // Flatten the tracked vehicles for each lanegroup
        if( link.model instanceof AbstractVehicleModel ) {
            link_per_veh.clear();
            for (AbstractLaneGroup lg: link.lanegroups_flwdn.values()) {
                VehicleLaneGroupTimer vehLgTimer = (VehicleLaneGroupTimer) lg.travel_timer;
                for (Long vehId : vehLgTimer.exit_times.keySet()){
                    link_per_veh.put(vehId, vehLgTimer.exit_times.get(vehId));
                }
            }
        }

        instantaneous_travel_time = link.lanegroups_flwdn.values().stream()
                .mapToDouble(lg->lg.travel_timer.get_mean_and_clear())
                .average().getAsDouble();
    }

}
