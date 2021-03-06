package profiles;

import error.OTMErrorLog;
import error.OTMException;
import utils.OTMUtils;

import java.util.*;

public class Profile2D {

    public float start_time;
    public Float dt;

    // for splits the key is link out id
    public Map<Long,List<Double>> values;
    public int num_times;

    ////////////////////////////////
    // construction
    ////////////////////////////////

    public Profile2D(float start_time, Float dt){
        this.start_time = start_time;
        this.dt = dt;
        values = new HashMap<>();
    }

    public void validate(OTMErrorLog errorLog){


        // start_time >= 0
        if(start_time<0)
            errorLog.addError("start_time<0");

        // dt >= 0
        if(dt!=null && dt<0)
            errorLog.addError("dt<0");

        // positivity
        for(List<Double> list : values.values() ) {
            if(list.size()>1 && dt==null)
                errorLog.addError("list.size()>1 && dt==null");
            if (Collections.min(list) < 0)
                errorLog.addError("Collections.min(list)<0");
        }

        // non NaNs
        for(List<Double> list : values.values() )
            for(Double x : list)
                if(Double.isNaN(x))
                    errorLog.addError("NaN in Profile2D.");

//        // all the same length
//        boolean isfirst = true;
//        boolean allequal = true;
//        int L = 0;
//        for(List<Double> list : values.values() ) {
//            if(isfirst)
//                L = list.size();
//            else
//                if(L!=list.size()) {
//                    errorLog.addError("L!=list.size()");
//                    allequal = false;
//                    break;
//                }
//        }

        // sum to 1
//        if(allequal)
//            for(int i=0;i<L;i++){
//                double x = 0d;
//                for(List<Double> list : values.values())
//                    x += list.get(i);
//                if(!OTMUtils.approximately_equals(x,1d))
//                    errorLog.addError(str + " : splits not adding to 1");
//            }

    }

    public void add_entry(long key, String content) throws OTMException {
        add_entry(key,OTMUtils.csv2list(content));
    }

    public void add_entry(long key,Double value) throws OTMException {
        List<Double> x = new ArrayList<>();
        x.add(value);
        add_entry(key,x);
    }

    public void add_entry(long key,List<Double> new_values) throws OTMException {
        if(have_key(key))
            throw new OTMException("repeated entry");
        if(values.isEmpty())
            this.num_times = new_values.size();
        else
            if(num_times!=new_values.size())
                throw new OTMException("wrong size entry.");
        values.put(key,new_values);
    }

    ////////////////////////////////
    // get
    ////////////////////////////////

    public List<Double> get(Long key){
        return values.get(key);
    }

    public int get_num_times(){
        return num_times;
    }

    public float get_dt(){
        return dt;
    }

    public boolean have_key(long key){
        return values.containsKey(key);
    }

    public Map<Long,Double> get_value_for_time(float time){
        if(time<start_time)
            return null;
        if(dt==null || dt==0)
            return get_ith_value(0);
        return get_ith_value((int)((time-start_time)/dt));
    }

    public TimeMap get_change_following(float now){
        if(now<start_time)
            return new TimeMap(start_time,get_ith_value(0));
        if(dt==null || dt==0)
            return null;

        int index = (int)((now+dt-start_time)/dt);
        if(index>num_times-1)
            return null;
        return new TimeMap(start_time + index*dt,get_ith_value(index));
    }

    // get values for time index i
    public Map<Long,Double> get_ith_value(int i){
        Map<Long,Double> r = new HashMap<>();
        int step = Math.max(0,Math.min(i,num_times-1));
        for(Map.Entry<Long,List<Double>> e : values.entrySet())
            r.put(e.getKey(),e.getValue().get(step));
        return r;
    }

    public Set<Long> get_nonzero_outlinks(){
        Set<Long> x = new HashSet<>();
        for(Map.Entry<Long,List<Double>> e : values.entrySet())
            if(!e.getValue().stream().allMatch(v->v==0d))
                x.add(e.getKey());
        return x;
    }

    ////////////////////////////////
    // other
    ////////////////////////////////

    public Profile2D clone() {
        Profile2D x = new Profile2D(this.start_time,this.dt);
        for(Map.Entry e : this.values.entrySet()){
            List<Double> v2 = new ArrayList<>();
            ((ArrayList<Double>)e.getValue()).forEach(z->v2.add(z));
            x.values.put((Long)e.getKey(),v2);
        }
        return x;
    }

}
