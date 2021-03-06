package dispatch;

import error.OTMException;
import core.Scenario;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public  class Dispatcher {

    public Scenario scenario;
    public float current_time;
    public float stop_time;
    public PriorityQueue<AbstractEvent> events;
    private boolean continue_simulation;
    public boolean verbose = false;

    public Map<Long,Integer> lg2deltalanes;    // tracks the total change in lane count caused by EventLaneGroupLane events.

    ///////////////////////////////////////////////////
    // construction
    ///////////////////////////////////////////////////

    public Dispatcher(){
        this.events = new PriorityQueue<>() ;
        this.continue_simulation = false;
        this.lg2deltalanes = new HashMap<>();
    }

    public void set_stop_time(float stop_time){
        this.stop_time = stop_time;
    }

    public void set_scenario(Scenario scenario){
        this.scenario = scenario;
    }

    public void initialize() throws OTMException {
        this.current_time = 0f;
        this.events.clear();
        this.continue_simulation = true;
    }

    public void set_continue_simulation(boolean x){
        this.continue_simulation = x;
    }

    ///////////////////////////////////////////////////
    // update
    ///////////////////////////////////////////////////

    public void remove_events_of_type(Class<? extends AbstractEvent> clazz){

        Set<AbstractEvent> remove = events.stream()
                .filter(x-> x.getClass()==clazz)
                .collect(toSet());

        events.removeAll(remove);
    }

    public void remove_events_for_recipient(Class<? extends AbstractEvent> clazz, Object recipient){

        Set<AbstractEvent> remove = events.stream()
                .filter(x->x.recipient==recipient && clazz.isAssignableFrom(x.getClass()) )
                .collect(toSet());

        events.removeAll(remove);
    }

    public void register_event(AbstractEvent event){
        if(event.timestamp<current_time) // || event.timestamp>end_time)
            return;
        events.offer(event);
    }

    public void dispatch_events_to_stop() throws OTMException {
        while( !events.isEmpty() && continue_simulation ) {
            AbstractEvent event = events.poll();
            current_time = event.timestamp;
            event.action();
        }
    }

    public void stop(){
        continue_simulation = false;
    }

    public void print_events(){
        this.events.stream().forEach(x->System.out.println(x.toString()));
    }

}
