package environnement.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import environnement.Description;
import environnement.utils.TilePos;

/**
 * An implementation of the BaseRepository which loads from a file.
 * <p>Descriptions are stored as: <code>"gridStr|prefX|prefY|weight|occurences|actionStr"</code>
 */
public class FileRepository extends BaseRepository {

    private Description[] data;

    public FileRepository(Path filePath) throws IOException {
        List<String> raw = Files.readAllLines(filePath);
        this.data = new Description[raw.size()];
        for (int k = 0; k < this.data.length; k++) {
            String[] tmp = raw.get(k).split("\\|");
            this.data[k] = new Description(tmp[0], Integer.parseInt(tmp[1]), Integer.parseInt(tmp[2]), Float.parseFloat(tmp[3]), Integer.parseInt(tmp[4]), tmp[5]);
        }
    }

    /**
     * Returns the string representation of the description.
     * @param d Description to dump.
     * @return the string dump.
     */
    public static String getStringDump(Description d) {
        TilePos p = d.getPreferredLocation();
        return d + "|" + p.x + "|" + p.y + "|" + d.getWeight() + "|" + d.getOccurences() + "|" + d.getAction();
    }

    /**
     * Returns the string representation of the descriptions in an array.
     * @param ds Descriptions to dump.
     * @return the string dumps in an array.
     */
    public static String[] getStringDump(Description...ds) {
        String[] r = new String[ds.length];

        for (int k = 0; k < r.length; k++)
            r[k] = FileRepository.getStringDump(ds[k]);

        return r;
    }

    @Override
    protected Description getNth(int n) {
        if (-1 < n && n < this.data.length)
            return this.data[n];
        return null;
    }

}
