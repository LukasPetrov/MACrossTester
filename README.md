# To-Do List

## method 1
##### cross = close old / create new order
- GUI console change to table
- store DataCube to binary file ?

- ordercounter in exit is useless?

````java
//save
FileOutputStream fos = new FileOutputStream("t.tmp");
ObjectOutputStream oos = new ObjectOutputStream(fos);
oos.writeObject(clubs);
oos.close();

//read
FileInputStream fis = new FileInputStream("t.tmp");
ObjectInputStream ois = new ObjectInputStream(fis);
List<Club> clubs = (List<Club>) ois.readObject();
ois.close();
````


## method 2 
#####Break Even, Trailing Stop, Partial Close, Max risk 2%, etc.
- add trailing stop






## Was is das ? (GUI)
````java
TestMainRepeater.getListOfParameters();
````

