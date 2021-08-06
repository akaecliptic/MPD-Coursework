package org.me.gcu.equake.Adapter;

import android.util.Xml;

import org.me.gcu.equake.Model.EQUAKE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Developed by: Michael A. F.
 */
public class EQUAKEParser {

    private static final String ns = null;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss");

    public List<EQUAKE> parse(InputStream in) throws XmlPullParserException, IOException {
        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(in, null);

            parser.nextTag(); // For null val
            parser.nextTag(); // For RSS Tag

            return readFeed(parser);
        }finally {
            in.close();
        }
    }

    private List<EQUAKE> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<EQUAKE> items = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, "channel");

        while (parser.next() != XmlPullParser.END_TAG){

            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String name = parser.getName();

            if (name.equals("item")) {
                    items.add(readItems(parser));
            }else {
                skip(parser);
            }

        }

        return items;
    }

    private EQUAKE readItems(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");

        parser.nextTag();

        return readEQUAKE(parser);
    }

    private EQUAKE readEQUAKE(XmlPullParser parser) throws  XmlPullParserException, IOException {

        String title = null;
        float[] description = new float[2];
        String link = null;
        LocalDate pubDate = null;
        float lat = 0;
        float lon = 0;

        while (parser.getEventType() != XmlPullParser.END_TAG) {

            String name = (parser.getName() == null) ? "" : parser.getName();

            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            switch (name) {
                case "title":
                    title = readTitle(parser);
                    break;
                case "description":
                    description = readDescription(parser);
                    break;
                case "link":
                    link = readLink(parser);
                    break;
                case "pubDate":
                    pubDate = readDate(parser);
                    break;
                case "lat":
                    lat = readLatLong(parser);
                    break;
                case "long":
                    lon = readLatLong(parser);
                    break;
                default:
                    skip(parser);
            }

            parser.nextTag();
        }

        return new EQUAKE(
                title,
                pubDate,
                lat,
                lon,
                description[1],
                description[0],
                link
        );
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        String temp = title.split(":")[2].split(", ")[0];
        title = temp.replace(",", ", ");
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    private float[] readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String rawText = readText(parser);
        float[] description = processDescription(rawText.toUpperCase().split(";"));
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }

    private float[] processDescription(String[] temp) {
        float[] toReturn = new float[2];
        for (String s : temp) {
            if(s.contains("DEPTH")){
                toReturn[0] = Float.parseFloat(s.split(" ")[2]);
            } else if(s.contains("MAGNITUDE")){
                toReturn[1] = Float.parseFloat(s.split(":")[1]);
            }
        }
        return toReturn;
    }

    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    private LocalDate readDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "pubDate");
        LocalDate date = LocalDate.parse(readText(parser), dateFormatter);
        parser.require(XmlPullParser.END_TAG, ns, "pubDate");
        return date;
    }

    private float readLatLong(XmlPullParser parser) throws IOException, XmlPullParserException {
        String name = parser.getName();
        parser.require(XmlPullParser.START_TAG, ns, name);
        float coordinate = Float.parseFloat(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, name);
        return coordinate;
    }


    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
