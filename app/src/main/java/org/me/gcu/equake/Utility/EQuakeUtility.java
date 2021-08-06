package org.me.gcu.equake.Utility;

import org.me.gcu.equake.Model.EQUAKE;
import org.me.gcu.equake.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Developed by: Michael A. F.
 */
public class EQuakeUtility {
    public static float getStrengthHue(float strength) {
        if(strength < 2){
            return 115;
        }
        else if(strength < 5){
            return 65;
        }
        else {
            return 0;
        }
    }

    public static String capitaliseTitle(String in) {
        StringBuilder out = new StringBuilder();

        for (String s : in.split(" ")) {
            out.append(s.charAt(0)).append(s.substring(1).toLowerCase()).append(" ");
        }
        
        return out.toString();
    }

    public static void sort(List<EQUAKE> list, SortType sort){
        switch (sort){
            case ALPHABET:
                list.sort(EQuakeUtility::sortByAlphabet);
                break;
            case MAGNITUDE:
                list.sort(EQuakeUtility::sortByMagnitude);
                break;
            case DEPTH:
                list.sort(EQuakeUtility::sortByDepth);
                break;
            default:
                list.sort(EQuakeUtility::sortByDate);
                break;
        }
    }

    public enum SortType{
        DATE(R.drawable.ic_sort_date), ALPHABET(R.drawable.ic_sort_alpha),
        MAGNITUDE(R.drawable.ic_sort_magnitude), DEPTH(R.drawable.ic_sort_depth);

        private final int icon;

        SortType(int resourceId){
            icon = resourceId;
        }

        public static SortType cycleSort(SortType val){
            List<SortType> list = Arrays.asList(values());
            int ind = list.indexOf(val) + 1;
            return (ind == list.size()) ? list.get(0) : list.get(ind);
        }

        public int getIcon(){
            return icon;
        }
    }

    private static int sortByDate(EQUAKE x, EQUAKE y) {
        return y.getTime().compareTo(x.getTime());
    }

    private static int sortByAlphabet(EQUAKE x, EQUAKE y) {
        return x.getLocation().compareTo(y.getLocation());
    }

    private static int sortByMagnitude(EQUAKE x, EQUAKE y) {
        return Float.compare(y.getMagnitude(), x.getMagnitude());
    }

    private static int sortByDepth(EQUAKE x, EQUAKE y) {
        return Float.compare(y.getDepth(), x.getDepth());
    }

    public enum FilterType{
        DEFAULT("Clear"),
        DIRECTIONS("Showing Most Cardinal Directions."),
        MAGNITUDE("Showing Greatest Magnitude."),
        DEPTH("Showing Deepest and Shallowest.");

        private final String message;

        FilterType(String text){
            message = text;
        }

        public String getMessage(){
            return message;
        }
    }

    public static List<EQUAKE> filter(List<EQUAKE> list, FilterType filter){
        switch (filter){
            case DIRECTIONS:
                return filterByDirections(list);
            case MAGNITUDE:
                return filterByMagnitude(list);
            case DEPTH:
                return filterByDepth(list);
            default:
                return list;
        }
    }

    private static List<EQUAKE> filterByDirections(List<EQUAKE> list){
        List<EQUAKE> working = new ArrayList<>(list);

        EQUAKE north = mostNorth(working);
        EQUAKE south = mostSouth(working);
        EQUAKE east = mostEast(working);
        EQUAKE west = mostWest(working);

        return Arrays.asList(north, south, east, west);
    }

    private static List<EQUAKE> filterByMagnitude(List<EQUAKE> list){
        List<EQUAKE> working = new ArrayList<>(list);
        working.sort(EQuakeUtility::sortByMagnitude);

        float limit = working.get(0).getMagnitude();
        working.removeIf(equake -> equake.getMagnitude() < limit);
        return working;
    }

    private static List<EQUAKE> filterByDepth(List<EQUAKE> list){
        List<EQUAKE> working = new ArrayList<>(list);
        working.sort(EQuakeUtility::sortByDepth);

        if (working.size() <= 2)
            return working;

        return Arrays.asList(working.get(0), working.get(working.size() - 1));
    }

    private static EQUAKE mostNorth(List<EQUAKE> list) {
        list.sort((x, y) -> Double.compare(y.getLatitude(), x.getLatitude()));
        return list.get(0);
    }

    private static EQUAKE mostSouth(List<EQUAKE> list) {
        list.sort((x, y) -> Double.compare(x.getLatitude(), y.getLatitude()));
        return list.get(0);
    }

    private static EQUAKE mostEast(List<EQUAKE> list) {
        list.sort((x, y) -> Double.compare(y.getLongitude(), x.getLongitude()));
        return list.get(0);
    }

    private static EQUAKE mostWest(List<EQUAKE> list) {
        list.sort((x, y) -> Double.compare(x.getLongitude(), y.getLongitude()));
        return list.get(0);
    }
}
