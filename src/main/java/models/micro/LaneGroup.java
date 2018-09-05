package models.micro;

import commodity.Commodity;
import common.*;
import error.OTMException;
import packet.AbstractPacketLaneGroup;

import java.util.Set;

public class LaneGroup  extends AbstractLaneGroup {

    public LaneGroup(Link link, Set<Integer> lanes, Set<RoadConnection> out_rcs) {
        super(link, lanes, out_rcs);
    }

    @Override
    public void add_commodity(Commodity commodity) {

    }

    @Override
    public void add_native_vehicle_packet(float timestamp, AbstractPacketLaneGroup vp) throws OTMException {
        System.out.format(timestamp + "\tadd_native_vehicle_packet");
    }

    @Override
    public void exiting_roadconnection_capacity_has_been_modified() {

    }

    @Override
    public void release_vehicle_packets(float timestamp) throws OTMException {
        System.out.format(timestamp + "\trelease_vehicle_packets");
    }

    @Override
    public float vehicles_for_commodity(Long commodity_id) {
        return 0;
    }

    @Override
    public float get_current_travel_time() {
        return Float.NaN;
    }

    @Override
    public double get_supply() {
        return 0;
    }

}