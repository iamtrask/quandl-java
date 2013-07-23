/**
 * Description of file content.
 *
 * @author atrask
 *         7/22/13
 */

import com.quandl.api.QDataset;
import com.quandl.api.QuandlConnection;
import org.junit.Test;

public class QDatasetIntegrationTest {


    QDataset dataset = new QuandlConnection().getDatasetBetweenDates("PRAGUESE/PX","2012-09-30","2012-11-29");

    @Test
    public void testId() {
        assert dataset.getId().equals("2422996");
    }

    @Test
    public void testSourceCode() {
        assert dataset.getSourceCode().equals("PRAGUESE");
    }

    @Test
    public void testCode() {
        assert dataset.getCode().equals("PX");
    }

    @Test
    public void testName() {
        assert dataset.getName().equals("Prague Stock Index: PX");
    }

    @Test
    public void testUrlize() {
        assert dataset.getUrlizeName().equals("Prague-Stock-Index-PX");
    }

    @Test
    public void testDescription() {
        assert dataset.getDescription().equals("Price index of blue chip issues. Base value: 1000 points. Start date: April 5, 1994. Number of base issues: variable.");
    }

    @Test
    public void testUpdatedAt() {
        assert dataset.getUpdatedAt().equals("2013-07-22T17:12:06Z");
    }

    @Test
    public void testFrequency() {
        assert dataset.getFrequency().equals("daily");
    }

    @Test
    public void testFromDate() {
        assert dataset.getFromDate().equals("1993-09-07");
    }

    @Test
    public void testToDate() {
        assert dataset.getToDate().equals("2013-07-22");
    }

    @Test
    public void testIsPrivate() {
        assert dataset.isPrivate() == false;
    }

    @Test
    public void testErrors() {
        assert dataset.getErrors().equals("{}");
    }

    @Test
    public void testDataset() {
        assert dataset.getDataset().size() == 44;
    }

}
