package environnement.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import environnement.Description;
import environnement.utils.TilePos;

/**
 * An implementation of the BaseRepository which loads from a file.
 * <p>Descriptions are stored as: <code>"gridStr|prefX|prefY|weight|occurences|actionStr"</code>
 */
public class FileRepository extends BaseRepository {

    private ArrayList<Description> data;

    public FileRepository() {
        this.data = new ArrayList<>();
    }

    public FileRepository(Path filePath) throws IOException {
        this();
        this.load(filePath);
    }

    public void load(Path filePath) throws IOException {
        for (String line : Files.readAllLines(filePath)) {
            String[] raw = line.split("\\|");
            Description d = new Description(raw[1], Integer.parseInt(raw[2]), Integer.parseInt(raw[3]), Float.parseFloat(raw[4]), Integer.parseInt(raw[5]), raw[6], raw[0]);
            this.data.add(d);
        }
    }

    public void save(Path filePath) throws IOException {
        String[] lines = FileRepository.getStringDump(this.data.toArray(new Description[this.data.size()]));
        String save = String.join("\n", lines);

        Files.write(filePath, save.getBytes());
    }

    /**
     * Returns the string representation of the description.
     * @param d Description to dump.
     * @return the string dump.
     */
    protected static String getStringDump(Description d) {
        TilePos p = d.getPreferredLocation();
        return d.tag + "|" + d + "|" + p.x + "|" + p.y + "|" + d.getWeight() + "|" + d.getOccurences() + "|" + d.getAction();
    }

    /**
     * Returns the string representation of the descriptions in an array.
     * @param ds Descriptions to dump.
     * @return the string dumps in an array.
     */
    protected static String[] getStringDump(Description...ds) {
        String[] r = new String[ds.length];

        for (int k = 0; k < r.length; k++)
            r[k] = FileRepository.getStringDump(ds[k]);

        return r;
    }

    @Override
    protected Description getNth(int n) {
        if (-1 < n && n < this.data.size())
            return this.data.get(n);
        return null;
    }

    @Override
    public Description getAny() {
        return this.data.get((int) (Math.random() * this.data.size()));
    }

    @Override
    public void add(Description d) {
        this.data.add(d);
    }

    @Override
    public void remove(Description d) {
        this.data.remove(d);
    }

    @Override
    public int count() {
        return this.data.size();
    }

}
