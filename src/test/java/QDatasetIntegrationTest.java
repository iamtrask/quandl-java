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
    public void testIdIntegration() {
        assert dataset.getId().equals("2422996");
    }

    @Test
    public void testSourceCodeIntegration() {
        assert dataset.getSourceCode().equals("PRAGUESE");
    }

    @Test
    public void testCodeIntegration() {
        assert dataset.getCode().equals("PX");
    }

    @Test
    public void testNameIntegration() {
        assert dataset.getName().equals("Prague Stock Index: PX");
    }

    @Test
    public void testUrlizeIntegration() {
        assert dataset.getUrlizeName().equals("Prague-Stock-Index-PX");
    }

    @Test
    public void testDescriptionIntegration() {
        assert dataset.getDescription().equals("Price index of blue chip issues. Base value: 1000 points. Start date: April 5, 1994. Number of base issues: variable.");
    }

    @Test
    public void testUpdatedAtIntegration() {
        assert dataset.getUpdatedAt().equals("2013-07-22T17:12:06Z");
    }

    @Test
    public void testFrequencyIntegration() {
        assert dataset.getFrequency().equals("daily");
    }

    @Test
    public void testFromDatIntegration() {
        assert dataset.getFromDate().equals("1993-09-07");
    }

    @Test
    public void testToDateIntegration() {
        assert dataset.getToDate().equals("2013-07-22");
    }

    @Test
    public void getColumnNamesIntegration() {
        System.out.println(dataset.getColumnNames());
        assert dataset.getColumnNames().get(0).equals("Date");
        assert dataset.getColumnNames().get(1).equals("Index");
        assert dataset.getColumnNames().get(2).equals("% Change");
    }

    @Test
    public void testIsPrivateIntegration() {
        assert dataset.isPrivate() == false;
    }

    @Test
    public void testErrorsIntegration() {
        assert dataset.getErrors().equals("{}");
    }

    @Test
    public void testDatasetIntegration() {
        assert dataset.getDataset().size() == 44;
    }

}
