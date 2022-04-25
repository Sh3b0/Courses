/*

Author: Ahmed Nouralla - Group: BS19-02 - a.shaaban@innopolis.university
   - Given 4 points A, B, C, D in 2D coordinate system, this program determines if the two line segments AB, CD intersect or not.
   - If the segments intersect, the program outputs the coordinates of the point of intersection.
   - Importance of this algorithm:
        This algorithm can have several geometrical/life applications, consider the situation when we have two flight trips
        each one goes between two airports and we want to know if there exists a country that both trips will pass on it.
        Here we can convert this problem into a Segment intersection problem where the airports are points and the segments
        are trip lines, then we can apply this algorithm to find the country that both of the trips will pass on it.
   - Sweep Line algorithms:
        It is the idea when we have several events, we can represent these evens as dots in 2D cartesian coordinate system,
        Then we sort these points/events according to some criteria. Then we create a "Sweep" line (Often vertical) That passes
        through all events from the lowest one in order to the largest one, and we process an event whenever the sweep line
        intersects the event. This technique is useful for solving problems that involve intersecting events.
        or some geometrical applications like the closest pair problem, the intersecting points problem, the convex hull problem.
   - Example problem:
        Given a set of employees, each one has arrival and leaving time, calculate the maximum number of employees
        that will be in the company at the same time.
   - Solution: We can represent the interval between arrival and leaving time of the employee as a horizontal line segment.
        Then we sort the segments by arrival time and pass on them using a sweep vertical line. Then We maintain a counter.
        Whenever we encounter a point of arrival we increase the counter by one. for leaving we decrement the counter.
        We keep a max variable that will hold the maximum value of the counter at any time and that will be the answer.
*/

import java.util.Scanner;

public class TwoSegmentsIntersection {

    // Checks if a point (x, y) lies on the segment between (x1,y1), (x2,y2)
    // The function assumes that all points lies on the same line.
    static boolean between(double x, double y, double x1, double y1, double x2, double y2) {
        return (x >= Math.min(x1, x2) && x <= Math.max(x1, x2) && y >= Math.min(y1, y2) && y <= Math.max(y1, y2));
    }

    public static void main(String[] args) {
        double Xa, Ya, Xb, Yb, Xc, Yc, Xd, Yd, Xp, Yp;
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) { // To allow several queries.
            Xa = sc.nextDouble();
            Ya = sc.nextDouble();
            Xb = sc.nextDouble();
            Yb = sc.nextDouble();
            Xc = sc.nextDouble();
            Yc = sc.nextDouble();
            Xd = sc.nextDouble();
            Yd = sc.nextDouble();
            final double den = (Xa - Xb) * (Yc - Yd) - (Ya - Yb) * (Xc - Xd);
            final double nom1 = ((Xa * Yb - Ya * Xb) * (Xc - Xd) - (Xa - Xb) * (Xc * Yd - Yc * Xd));
            final double nom2 = ((Xa * Yb - Ya * Xb) * (Yc - Yd) - (Ya - Yb) * (Xc * Yd - Yc * Xd));

            // Segments have the same slope, They are either parallel or coincident or they share an end.
            if (den == 0.0) {
                if (nom1 == 0 || nom2 == 0) {
                    if ((Xa == Xc && Ya == Yc) || (Xa == Xd && Ya == Yd))
                        System.out.println("Segments share an end at (" + Xa + ", " + Ya + ")");
                    else if ((Xb == Xc && Yb == Yc) || (Xb == Xd && Yb == Yd))
                        System.out.println("Segments share an end at (" + Xb + ", " + Yb + ")");
                    else if (between(Xa, Ya, Xc, Yc, Xd, Yd) || between(Xb, Yb, Xc, Yc, Xd, Yd) || between(Xc, Yc, Xa, Ya, Xb, Yb) || between(Xd, Yd, Xa, Ya, Xb, Yb))
                        System.out.println("Segments are coincident");
                    else
                        System.out.println("Segments are parallel");

                } else {
                    System.out.println("Segments are parallel");
                }
            } else { // Segments have different slope, Either they intersect or not, but they can't be parallel or coincident.
                Xp = nom1 / den;
                Yp = nom2 / den;

                // To ignore small errors
                if (Math.abs(Xp) < 0.001) Xp = 0.0;
                if (Math.abs(Yp) < 0.001) Yp = 0.0;

                // Check if the point of intersection lies on both of the segments.
                if (between(Xp, Yp, Xa, Ya, Xb, Yb) && between(Xp, Yp, Xc, Yc, Xd, Yd))
                    System.out.println("Intersection at (" + Xp + ", " + Yp + ")");
                else
                    System.out.println("Segments are not parallel and don't intersect");
            }
        }
    }
}