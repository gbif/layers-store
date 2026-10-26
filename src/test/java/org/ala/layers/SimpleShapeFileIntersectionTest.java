package org.ala.layers;

import au.org.ala.layers.intersect.SimpleShapeFile;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class SimpleShapeFileIntersectionTest {

    SimpleShapeFile simpleShapeFile;
    String[] idColumnLookup;
    String[] titleColumnLookup;
    int[] idColumnIndex;
    int[] titleColumnIndex;

    @Test
    public void testSimpleShapeFileIntersection() throws URISyntaxException {
        URL url = getClass().getClassLoader().getResource("gadm2_subdivided_bratest.shp");
        String filePrefix = new File(url.toURI()).getPath().replace(".shp", "");
        simpleShapeFile = new SimpleShapeFile(filePrefix, new String[]{"gid_0", "gid_1", "gid_2", "gid_3", "name_0", "name_1", "name_2", "name_3", "isoCountry"});

        idColumnLookup = simpleShapeFile.getColumnLookup(2);
        titleColumnLookup = simpleShapeFile.getColumnLookup(6);
        idColumnIndex = simpleShapeFile.getColumnIdxs("gid_2");
        titleColumnIndex = simpleShapeFile.getColumnIdxs("name_2");

        double longitude = -61.50063994508639;
        double latitude = -8.129791605459658;

        Assert.assertEquals(closestShape(longitude, latitude, 0.2), "BRA.4.39_2");
        Assert.assertEquals(closestShape(longitude, latitude, 0.4), "BRA.4.39_2");
        Assert.assertEquals(closestShape(longitude, latitude, 0.6), "BRA.4.39_2");
        Assert.assertEquals(closestShape(longitude, latitude, 0.8), "BRA.4.39_2");
        Assert.assertEquals(closestShape(longitude, latitude, 1.0), "BRA.4.39_2");

        Assert.assertNull(closestShape(-64.75, -20.89, 0.4));
    }

    String closestShape(double longitude, double latitude, double uncertainty) {
        List<ImmutablePair<Integer, Double>> intersections = simpleShapeFile.intersectInt(longitude, latitude, uncertainty);
        ImmutablePair<Integer, Double> closest = null;
        for (ImmutablePair<Integer, Double> intersection : intersections) {
            double distance = intersection.getRight();

            if (closest == null || distance < closest.getRight()) {
                closest = intersection;
            }
        }
        if  (closest != null) {
            return idColumnLookup[idColumnIndex[closest.getLeft()]];
        } else {
            return null;
        }
    }
}
