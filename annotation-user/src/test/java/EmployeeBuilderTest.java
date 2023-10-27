import org.example.Employee;
import org.example.EmployeeBuilder;
import org.junit.Assert;
import org.junit.Test;

public class EmployeeBuilderTest {

    @Test
    public void employeeBuilderTest() {
        Employee employee = new EmployeeBuilder().name("Test").age(12).build();
        Assert.assertEquals("Test", employee.getName());
        Assert.assertEquals(12, employee.getAge());
    }
}
