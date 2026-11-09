package com.mycompany.barangayrecordsmanagementsystem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * One entry of the "Resident" drop-down used by the Certificate and Blotter
 * screens. It shows "ID - Last, First" but remembers the resident_id, which is
 * the value saved in the foreign key column.
 */
public class ResidentItem {

    private final int id;
    private final String name;

    public ResidentItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    @Override
    public String toString() {
        return id + " - " + name;
    }

    public static List<ResidentItem> loadAll() throws SQLException {
        List<ResidentItem> items = new ArrayList<>();
        for (Resident r : DAO.getAll()) {
            String middle = (r.getMiddleName() == null || r.getMiddleName().isEmpty())
                    ? "" : " " + r.getMiddleName();
            items.add(new ResidentItem(r.getResidentId(),
                    r.getLastName() + ", " + r.getFirstName() + middle));
        }
        Collections.reverse(items);   // DAO.getAll() is newest first; show oldest first
        return items;
    }
}
