package pii.marioagent.environnement.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import pii.marioagent.environnement.Description;
import pii.marioagent.environnement.utils.TilePos;

/**
 * An implementation of the ListRepository which loads from a file.
 * <p> Descriptions are stored as: <code>"tag|gridStr|prefX|prefY|weight|occurences|actionStr"</code>
 */
public class FileRepository extends ListRepository {

    public FileRepository() {
        super();
    }

    public FileRepository(Path filePath) throws IOException {
        super();
        this.load(filePath);
    }

    public void load(Path filePath) throws IOException {
        List<String> lines = Files.readAllLines(filePath);

        Description[] load = FileRepository.getDescriptionLoad(lines.toArray(new String[lines.size()]));
        super.add(load);

        super.sort();
    }

    public void save(Path filePath) throws IOException {
        super.sort();

        String[] lines = FileRepository.getStringDump(super.getAll());
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

    protected static Description getDescriptionLoad(String d) {
        String[] raw = d.split("\\|");
        return new Description(raw[1], Integer.parseInt(raw[2]), Integer.parseInt(raw[3]), Float.parseFloat(raw[4]), Integer.parseInt(raw[5]), raw[6], raw[0]);
    }

    /**
     * Returns the string representation of the descriptions in an array.
     * @param des Descriptions to dump.
     * @return the string dumps in an array.
     */
    protected static String[] getStringDump(Description... des) {
        String[] r = new String[des.length];

        for (int k = 0; k < r.length; k++)
            r[k] = FileRepository.getStringDump(des[k]);

        return r;
    }

    protected static Description[] getDescriptionLoad(String... des) {
        ArrayList<Description> r = new ArrayList<Description>();

        for (int k = 0; k < des.length; k++)
            if (!des[k].startsWith("//"))
                r.add(FileRepository.getDescriptionLoad(des[k]));

        return r.toArray(new Description[r.size()]);
    }

}
