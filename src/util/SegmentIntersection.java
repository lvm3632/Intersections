// Hecho por Michel Lujano A01636172
// SegmentIntersection.java

package util;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class SegmentIntersection {
    public final int INNERPOINT = 1;
    public final int ENDPOINT = 2;

    private final int N;
    private Segment[] S;
    private HashMap<Point2D, Intersection> intersections;
    private RedBlackBST<Double, Segment> status;
    private RedBlackBST<Point2D, Segment> events;

    private class Intersection {
        private ArrayList<Integer> segments;
        private ArrayList<Integer> intersectionTypes;
        private final Point2D intersectionPoint;

        public Intersection(Point2D point) {
            segments = new ArrayList<>();
            intersectionTypes = new ArrayList<>();
            intersectionPoint = point;
        }

        public void addSegment(int segment, int intersectionType) {
            segments.add(segment);
            intersectionTypes.add(intersectionType);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Intersection at (%.2f, %.2f)\n",intersectionPoint.x, intersectionPoint.y));
            for (int i = 0; i < segments.size(); i++) {
                sb.append(String.format("Segment %d: %s\n",
                        segments.get(i), intersectionTypes.get(i) == INNERPOINT ? "Innerpoint" : "Endpoint"));
            }
            return sb.toString();
        }




    }

    public SegmentIntersection(In in) {
        if (in == null) throw new IllegalArgumentException("argument is null");

        try {
            N = in.readInt();
            S = new Segment[N];
            status = new RedBlackBST<>();
            events = new RedBlackBST<>();
            intersections = new HashMap<>();

            for (int i = 0; i < N; i++) {
                double x1 = in.readDouble();
                double y1 = in.readDouble();
                double x2 = in.readDouble();
                double y2 = in.readDouble();
                Segment s = new Segment(i, new Point2D(x1, y1), new Point2D(x2, y2));
                S[i] = s;
            }
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("invalid input format in SegmentIntersection constructor", e);
        }
        obtainIntersections();
    }

    public void obtainIntersections(){
        /* Initializa event points with segment end points */
        for (Segment s: S) {
            events.put(s.upper(), s);
            events.put(s.lower(), s);
        }
        while (!events.isEmpty()) {

            Point2D p = events.min();

            Segment s = events.get(p);
            events.deleteMin();
            handleEventPoint(s, p);
        }
    }

    private void handleEventPoint(Segment s, Point2D p) {


        if (s.isSinglePoint())
        { // intersection event
           // System.out.println("ENTRA ANTES?");
            StdOut.println("Intersection point " + p.toString());
           updateStatus(s.upper().y);


            if(getLeftNeighbor(s) != null && getRightNeighbor(s) != null)
            {


                Segment leftInters = getLeftNeighbor(s);
                Segment rightInters = getRightNeighbor(s);

                status.delete(rightInters.upper().x);
                status.delete(leftInters.upper().x);

            Segment leftleftInters = getLeftNeighbor(leftInters);
            Segment rightrightInters = getRightNeighbor(rightInters);


            status.put(rightInters.upper().x, leftInters);
            status.put(leftInters.upper().x, rightInters);


            if (rightrightInters != null && leftleftInters != null && doIntersect(leftleftInters, rightrightInters))
            {

               // System.out.println("ENTRO A RIGHT RIGHT???");
                System.out.println(rightrightInters.upper().x);
                System.out.println(rightrightInters.upper().y);
                System.out.println(rightrightInters.lower().x);
                System.out.println(rightrightInters.lower().y);

                System.out.println("-----------------------");


               // System.out.println(s.getId());
                System.out.println(leftleftInters.upper().x);
                System.out.println(leftleftInters.upper().y);
                System.out.println(leftleftInters.lower().x);
                System.out.println(leftleftInters.lower().y);
                /*System.out.println(s.upper().x + "SEGMENTO X");
                System.out.println(s.upper().y + "SEGMENTO Y");
                System.out.println(s.lower().x + "SEGMENTO X");
                System.out.println(s.lower().y + "SEGMENTO Y");*/

                Point2D intrsPoint = getIntersectionPoint(leftleftInters, rightrightInters);
                System.out.println(intrsPoint);
                Intersection intersection = new Intersection(intrsPoint);

               // if(rightrightInters.upper().y > leftleftInters.upper().y)

                   // System.out.println("HOLA RIGHT");
                    intersection.addSegment(rightrightInters.getId(),
                            (rightrightInters.upper().equals(intrsPoint) ? ENDPOINT :
                                   rightrightInters.lower().equals(intrsPoint) ? ENDPOINT : INNERPOINT));
                    intersection.addSegment(leftleftInters.getId(),
                            (leftleftInters.upper().equals(intrsPoint) ? ENDPOINT :
                                    leftleftInters.lower().equals(intrsPoint) ? ENDPOINT : INNERPOINT));

                    if (!intersections.containsKey(intrsPoint)) {
                        intersections.put(intrsPoint, intersection);
                    }


                Segment intersectionEvent = new Segment(-1, intrsPoint, intrsPoint);
                events.put(intrsPoint, intersectionEvent);


                //updateStatus(intersectionEvent.getCurrentX(intrsPoint.y));
            }

            }

        } else if (s.upper().equals(p))
        {

        //upper end point event

            StdOut.println("Upper end point " + p.toString());
            //Update the status with correct x coordinates
            updateStatus(p.y);

            Segment leftNeighbor = getLeftNeighbor(s);
            Segment rightNeighbor = getRightNeighbor(s);

            status.put(s.upper().x, s);


            if (leftNeighbor != null && doIntersect(leftNeighbor, s)) {

                Point2D intrsPoint = getIntersectionPoint(leftNeighbor, s);
                Intersection intersection = new Intersection(intrsPoint);
                intersection.addSegment(leftNeighbor.getId(),
                        (leftNeighbor.upper().equals(intrsPoint) ? ENDPOINT :
                                leftNeighbor.lower().equals(intrsPoint) ? ENDPOINT : INNERPOINT));
                intersection.addSegment(s.getId(),
                        (s.upper().equals(intrsPoint) ? ENDPOINT :
                                s.lower().equals(intrsPoint) ? ENDPOINT : INNERPOINT));

                if (!intersections.containsKey(intrsPoint)) {
                    intersections.put(intrsPoint, intersection);

                }


                Segment intersectionEvent = new Segment(-1, intrsPoint, intrsPoint);
                events.put(intrsPoint, intersectionEvent);
                //updateStatus(intersectionEvent.getCurrentX(intrsPoint.y));
            }

            if (rightNeighbor != null && doIntersect(s, rightNeighbor)) {

                Point2D intrsPoint = getIntersectionPoint(s, rightNeighbor);
                Intersection intersection = new Intersection(intrsPoint);

                intersection.addSegment(s.getId(),
                        (s.upper().equals(intrsPoint) ? ENDPOINT :
                                s.lower().equals(intrsPoint) ? ENDPOINT : INNERPOINT));
                intersection.addSegment(rightNeighbor.getId(),
                        (rightNeighbor.upper().equals(intrsPoint) ? ENDPOINT :
                                rightNeighbor.lower().equals(intrsPoint) ? ENDPOINT : INNERPOINT));

                if (!intersections.containsKey(intrsPoint)) {
                    intersections.put(intrsPoint, intersection);
                }


                Segment intersectionEvent = new Segment(-1, intrsPoint, intrsPoint);
                events.put(intrsPoint, intersectionEvent);
               //updateStatus(intersectionEvent.getCurrentX(intrsPoint.y));

            }


        } else if(s.lower().equals(p)  )
        { //lower end point event
            StdOut.println("Lower end point " + p.toString());
           updateStatus(p.y);

            //s y //p
            //1. Search with p in T, and delete s
            // 2. Let Sleft and SRight, be the left and right neighbors of s
            //in T (before deletion).
            Segment leftNeighbor = getLeftNeighbor(s);
            Segment rightNeighbor = getRightNeighbor(s);
            //if(leftNeighbor != null)
            status.put(s.lower().x, leftNeighbor);
            //if(rightNeighbor != null)
            status.put(s.upper().x, rightNeighbor);

            status.delete(s.upper().x);



            if (rightNeighbor != null && doIntersect(s, rightNeighbor) && !rightNeighbor.equals(s)) {
                // Create and add the intersection to the intersection hash table
                Point2D intrsPoint = getIntersectionPoint(s, rightNeighbor);
                Intersection intersection = new Intersection(intrsPoint);

                intersection.addSegment(s.getId(),
                        (s.upper().equals(intrsPoint) ? ENDPOINT :
                                s.lower().equals(intrsPoint) ? ENDPOINT : INNERPOINT));
                intersection.addSegment(rightNeighbor.getId(),
                        (rightNeighbor.upper().equals(intrsPoint) ? ENDPOINT :
                                rightNeighbor.lower().equals(intrsPoint) ? ENDPOINT : INNERPOINT));

                if (!intersections.containsKey(intrsPoint)) {
                    intersections.put(intrsPoint, intersection);
                }

                // Add the intersection to the events list
                Segment intersectionEvent = new Segment(-1, intrsPoint, intrsPoint);
                events.put(intrsPoint, intersectionEvent);
              //updateStatus(intersectionEvent.getCurrentX(intrsPoint.y));
            }

            if (leftNeighbor != null && doIntersect(leftNeighbor, s) && !leftNeighbor.equals(s)) {
                // Create and add the intersection to the intersection hash table
                Point2D intrsPoint = getIntersectionPoint(leftNeighbor, s);

                Intersection intersection = new Intersection(intrsPoint);
                intersection.addSegment(leftNeighbor.getId(),
                        (leftNeighbor.upper().equals(intrsPoint) ? ENDPOINT :
                                leftNeighbor.lower().equals(intrsPoint) ? ENDPOINT : INNERPOINT));
                intersection.addSegment(s.getId(),
                        (s.upper().equals(intrsPoint) ? ENDPOINT :
                                s.lower().equals(intrsPoint) ? ENDPOINT : INNERPOINT));

                if (!intersections.containsKey(intrsPoint)) {
                    intersections.put(intrsPoint, intersection);
                }

                 //Add the intersection to the events list
                Segment intersectionEvent = new Segment(-1, intrsPoint, intrsPoint);
                events.put(intrsPoint, intersectionEvent);
               // updateStatus(intersectionEvent.getCurrentX(intrsPoint.y));
            }


        }

    }

    private Segment getRightNeighbor (Segment s) {
        Segment rightNeighbor = null;
        if (!status.isEmpty()) {
            Double rightKey = status.ceiling(s.upper().x);
            if (rightKey != null) rightNeighbor = status.get(rightKey);
        }
        return rightNeighbor;
    }

    private Segment getLeftNeighbor(Segment s) {
        Segment leftNeighbor = null;
        if (!status.isEmpty()) {
            Double leftKey = status.floor(s.upper().x);
            if (leftKey != null) leftNeighbor = status.get(leftKey);
        }
        return leftNeighbor;
    }

    private boolean doIntersect(Segment a, Segment b) {
        double det1 = a.upper().x * (a.lower().y - b.lower().y) - a.upper().y * (a.lower().x - b.lower().x)
                + a.lower().x * b.lower().y - a.lower().y * b.lower().x;
        double det2 = a.upper().x * (a.lower().y - b.upper().y) - a.upper().y * (a.lower().x - b.upper().x)
                + a.lower().x * b.upper().y - a.lower().y * b.upper().x;
        if (det1 == 0.0 && det2 == 0.0) {
            return true;
        } else if ((det1 > 0.0 && det2 > 0.0) || (det1 < 0.0 && det2 < 0.0)) {
            return false;
        } else {
            det1 = b.upper().x * (b.lower().y - a.lower().y) - b.upper().y * (b.lower().x - a.lower().x)
                    + b.lower().x * a.lower().y - b.lower().y * a.lower().x;
            det2 = b.upper().x * (b.lower().y - a.upper().y) - b.upper().y * (b.lower().x - a.upper().x)
                    + b.lower().x * a.upper().y - b.lower().y * a.upper().x;
            return (!(det1 > 0.0) || !(det2 > 0.0)) && (!(det1 < 0.0) || !(det2 < 0.0));
        }
    }

    private Point2D getIntersectionPoint(Segment a, Segment b) {
        double ma = (a.upper().y - a.lower().y) / (a.upper().x - a.lower().x);
        double mb = (b.upper().y - b.lower().y) / (b.upper().x - b.lower().x);
        double x = (ma * a.upper().x - mb * b.upper().x + b.upper().y - a.upper().y) / (ma - mb);
        double y = (ma * mb * (b.upper().x - a.upper().x) + mb * a.upper().y - ma * b.upper().y) / (mb - ma);
        return new Point2D(x, y);
    }

    //This function updates the current x coordinate of all the segments in the status
    private void updateStatus(double y) {
        if (!status.isEmpty()) {
            for (double x : status.keys()) {
                Segment s = status.get(x);
                double currentX = s.getCurrentX(y);
                status.delete(x);
                status.put(currentX, s);

            }
        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Segment s: S) {
            sb.append(String.format("Segment %d: %s\n", i++, s.toString()));
        }
        for (Intersection intersection: intersections.values()) {
            sb.append(intersection.toString());
        }
        return sb.toString();
    }

    public void drawSegments()
    {
       // System.out.println("hola");

        StdDraw.setCanvasSize(1024, 768);
        StdDraw.setXscale(0, 2048);
        StdDraw.setYscale(0, 1536);
        //StdDraw.setXscale(0, 4000);
        //StdDraw.setYscale(0, 4000);

        StdDraw.setPenRadius(.0050);
        StdDraw.setPenColor(StdDraw.BLACK);

        /*StdDraw.point(1000,1000);
        StdDraw.point(1000,1000);
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.line(
                2803,
                2461,
                4131,
                2565
        );*/
        for (int i=0; i<S.length;i++)
        {

            Point2D puntoA = new Point2D(S[i].upper().x, S[i].upper().y);
            Point2D puntoB = new Point2D(S[i].lower().x, S[i].lower().y);

           // Point2D puntoC = new Point2D(S[i-1].upper().x, S[i-1].upper().y);
           // Point2D puntoD = new Point2D(S[i-1].lower().x, S[i-1].lower().y);


            StdDraw.point(S[i].upper().x*100, S[i].upper().y*100);
            StdDraw.point(S[i].lower().x*100, S[i].lower().y*100);
            String indice = Integer.toString(i);

            StdDraw.text(S[i].upper().x*100, S[i].upper().y*100,"Segmento: " + indice);
            StdDraw.line(S[i].upper().x*100, S[i].upper().y*100,S[i].lower().x*100, S[i].lower().y*100 );

         //   StdDraw.line(S[i].upper().x*100, S[i].upper().y*100, S[i+1].lower().x*100, S[i+1].lower().y*100);

        }


        StdDraw.setPenRadius(.025);

        for (Intersection intersection: intersections.values()) {

            String y = String.format("%.2f", intersection.intersectionPoint.y);
            String x = String.format("%.2f", intersection.intersectionPoint.x);
            System.out.println(intersection.intersectionPoint + "PUNTO");
            if(intersection.intersectionTypes.get(1) == 2 || intersection.intersectionTypes.get(0) == 2)
            {
                StdDraw.setPenColor(StdDraw.GREEN);
            }else StdDraw.setPenColor(StdDraw.RED);

            StdDraw.setPenRadius(.025);
            StdDraw.point(intersection.intersectionPoint.x*100, intersection.intersectionPoint.y*100);
            StdDraw.setPenRadius(.085);
            StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
            StdDraw.text(intersection.intersectionPoint.x*95, intersection.intersectionPoint.y*96,"(+"+ x +","+ y + ")");

            System.out.println(intersection.intersectionTypes + "TIPO");

        }


    }

    public static void main(String[] args) {
        In in = new In("./SegmentsTest.txt");
        SegmentIntersection si = new SegmentIntersection(in);
        System.out.println( si.toString());
        //3.0 13.0 7.0 9.0
        //9.0 13.0 6.0 9.0

        Point2D punto1 = new Point2D(3.0, 13.0);
        Point2D punto2 = new Point2D(7.0, 9.0);

        Segment a = new Segment(-1, punto1,punto2);

        Point2D punto3 = new Point2D(9.0, 13.0);
        Point2D punto4 = new Point2D(6.0, 9.0);

        Segment b = new Segment(-1, punto3,punto4);

        si.drawSegments();
        //Point2D punto = si.getIntersectionPoint(a,b);

       // System.out.println(punto.toString());


    }
}
