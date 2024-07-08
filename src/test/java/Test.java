import entity.User;
import org.estivate.SimpleORM;

public class Test {
    SimpleORM orm = new SimpleORM();

    void test(){
        try {
            User user = orm.findById(User.class, 1); // Assuming there's a user with id = 1
            if (user != null) {
                System.out.println("User Found: " + user.getName());
            } else {
                System.out.println("User Not Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
