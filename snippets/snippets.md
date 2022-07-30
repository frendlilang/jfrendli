# Frendli Snippets

Below are the copyable code snippets that are also shown with syntax highlighting in the images.

## Variables, Data Types, and Literals

```
create age = 30               // number
create price = 4.25           // number
create isValid = true         // boolean
create isOnline = false       // boolean
create firstName = "Jane"     // text
create middleName = empty     // empty
change middleName = "Mary"    // text
```

## Control Flow

#### Selection

```
// Selection

create score = 5
create message = ""

if score > 10
    change message = "Very Good"
otherwise if score > 5
    change message = "Good"
otherwise
    change message = "Bad"
```

#### Bounded Loop

```
// Bounded loop

create count = 0

repeat 5 times
    change count = count + 1
    display(send count)
```

#### Unbounded Loop

```
// Unbounded loop

create max = 5
create count = 0

repeat while count < max
    change count = count + 1
    display(send count)
```

## Functions

```
// Functions

define add(accept a, b)
    create result = a + b
    return with result

create result = add(send 2, 3)
display(send result)
```

## OOP

*(paused feature, may be removed)*

```
describe Point
    has x
    has y

    define onNew(accept x, y)
        change me.x = x
        change me.y = y

    define move(accept x, y)
        change me.x = me.x + x
        change me.y = me.y + y

create pointA = new Point(send 1, 2)
pointA.move(send 3, 4)
```
