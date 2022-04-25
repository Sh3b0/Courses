# Project design has changed to follow the SOLID principles:

- **Single Responsibility Principle**: `Human` class has now a single and clear responsibility, as well as other classes in the project.  

- **Open/Closed Principle**: Languages are now open for extension, by creating a new language that implements interface `Language` and
  implementing `HelloWord` method that returns the word `"Hello"` in that language.

- **Liskov Substitution Principle**: Classes `ActiveHuman`, `EmployableHuman`, and `ReligiousHuman` are now subtypes of class `Human` and everything that
  a `Human` can do is by definition doable by instances of these classes.
  
- **Interface Segregation Principle**: Instances of class `Human` are not forced to depend on methods they do not need (i.e., class `Human` implements
  only necessary methods that every human needs).  
  
- **Dependency Inversion Principle**: High-level module `Human` doesn't depend on any low-level module (i.e., enum `Languages`), instead, both of them
  depend on an abstraction (i.e., interface `Language`)

