/**
 * Description of file content.
 *
 * @author atrask
 *         7/22/13
 */

import com.quandl.api.java.QDataset;
import org.junit.Test;

public class QDatasetTest {

    QDataset dataset = new QDataset("{\"id\":2422996,\"source_code\":\"PRAGUESE\",\"code\":\"PX\",\"name\":\"Prague Stock Index: PX\",\"urlize_name\":\"Prague-Stock-Index-PX\",\"description\":\"Price index of blue chip issues. Base value: 1000 points. Start date: April 5, 1994. Number of base issues: variable.\",\"updated_at\":\"2013-07-22T17:12:06Z\",\"frequency\":\"daily\",\"from_date\":\"1993-09-07\",\"to_date\":\"2013-07-22\",\"column_names\":[\"Date\",\"Index\",\"% Change\"],\"private\":false,\"type\":null,\"errors\":{},\"data\":[[\"2012-11-29\",992.1,1.69],[\"2012-11-28\",975.6,-1.96],[\"2012-11-27\",995.1,1.21],[\"2012-11-26\",983.3,0.65],[\"2012-11-23\",976.9,1.43],[\"2012-11-22\",963.1,0.05],[\"2012-11-21\",962.6,-0.77],[\"2012-11-20\",970.2,-0.45],[\"2012-11-19\",974.5,0.44],[\"2012-11-16\",970.2,0.09],[\"2012-11-15\",969.4,-0.51],[\"2012-11-14\",974.3,0.84],[\"2012-11-13\",966.2,-1.17],[\"2012-11-12\",977.6,-0.22],[\"2012-11-09\",979.8,-0.57],[\"2012-11-08\",985.4,-0.4],[\"2012-11-07\",989.4,-0.62],[\"2012-11-06\",995.5,0.74],[\"2012-11-05\",988.2,0.74],[\"2012-11-02\",980.9,0.53],[\"2012-11-01\",975.8,0.5],[\"2012-10-31\",970.9,0.14],[\"2012-10-30\",969.5,1.1],[\"2012-10-29\",959.0,-0.54],[\"2012-10-26\",964.2,-1.29],[\"2012-10-25\",976.8,0.05],[\"2012-10-24\",976.2,0.4],[\"2012-10-23\",972.3,-1.78],[\"2012-10-22\",990.0,0.17],[\"2012-10-19\",988.3,-0.54],[\"2012-10-18\",993.7,-0.18],[\"2012-10-17\",995.5,0.08],[\"2012-10-16\",994.7,0.79],[\"2012-10-15\",986.9,0.09],[\"2012-10-12\",985.9,0.03],[\"2012-10-11\",985.7,0.21],[\"2012-10-10\",983.6,-0.24],[\"2012-10-09\",986.0,0.04],[\"2012-10-08\",985.5,-0.02],[\"2012-10-05\",985.7,1.77],[\"2012-10-04\",968.5,0.5],[\"2012-10-03\",963.7,0.34],[\"2012-10-02\",960.4,-0.75],[\"2012-10-01\",967.7,1.5]]}","");

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
    public void getColumnNames() {
        System.out.println(dataset.getColumnNames());
        assert dataset.getColumnNames().get(0).equals("Date");
        assert dataset.getColumnNames().get(1).equals("Index");
        assert dataset.getColumnNames().get(2).equals("% Change");
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
