package pii.marioagent.environnement.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.Consumer;

import pii.marioagent.environnement.Description;
import pii.marioagent.environnement.utils.TilePos;

/**
 * An implementation of the BaseRepository which loads from a file.
 * <p>Descriptions are stored as: <code>"gridStr|prefX|prefY|weight|occurences|actionStr"</code>
 */
public class FileRepository extends BaseRepository {

    private ArrayList<Description> data;
    private boolean sorted;

    public FileRepository() {
        this.data = new ArrayList<>();
        this.sorted = false;
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
        if (!this.sorted) this.sort();
    }

    public void save(Path filePath) throws IOException {
        if (!this.sorted) this.sort();

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

    protected void sort() {
        this.data.sort((Description e1, Description e2) -> (int) (e2.getWeight() - e1.getWeight()));
        this.sorted = true;
    }

    @Override
    protected Description getNth(int n) {
        if (-1 < n && n < this.data.size()) {
            if (!this.sorted) this.sort();
            return this.data.get(n);
        }
        return null;
    }

    @Override
    public void add(Description d) {
        this.sorted = false;
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
