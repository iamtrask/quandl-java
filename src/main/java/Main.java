
public class Main {

    public final static void main(String[] args) throws Exception {

        QuandlConnection q = new QuandlConnection("MNWDqjRSwW6348frGCEo");
       q.getDatasetBetweenDates("DOE/RWTC","2005-06-30","2013-07-16");
    }

}
