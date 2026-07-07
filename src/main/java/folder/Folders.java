package folder;

import Users.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

// id
// name
// parentFolder
// owner
// createdAt

@Entity
@Table(name = "folders")
public class Folders {
    
    public Folders(){

    }


    private Long id;
    private String name;
    private Folders parentFolder;
    private User owner;
    private LocalDate createdAt;
}