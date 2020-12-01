import org.junit.Test;

import javax.imageio.ImageIO;
import java.util.Arrays;

public class IoTest {

    @Test
    public void tt(){
        String readFormats[] = ImageIO.getReaderFormatNames();
        String writeFormats[] = ImageIO.getWriterFormatNames();
        System.out.println("Readers: " + Arrays.asList(readFormats));
        System.out.println("Writers: " + Arrays.asList(writeFormats));
    }
}
