package data;
import database.*;
public class DiscreteItem extends Item{

    DiscreteItem(DiscreteAttribute attribute, String value){
        super(attribute, value);

    }
    double distance (Object a){
        Item item = (Item) a;
        if (this.getValue().equals(item.getValue())){
            return 0;
        }else{
            return 1;
        }
    }


}
