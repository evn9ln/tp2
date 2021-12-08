import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;


@XmlType(name = "ArraysOfWarriors")
@XmlRootElement
public class ArrayOfWarriors {

    @XmlElement(name = "array", required = true)
    public List<Integer> array;

    @XmlElement(name = "msg", required = true)
    public String msg;

    public ArrayOfWarriors() {
    }

    public ArrayOfWarriors(List<Integer> array, String msg) {
        this.array = array;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "\n" + msg + "\n" + array + "\n";
    }
}
