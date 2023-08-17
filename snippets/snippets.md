# Frendli Code Snippets

Below are the copyable code snippets that are also shown with syntax highlighting in the images.

## Variables, Data Types, and Literals

```
create age = 30               // number
create price = 4.25           // number
create isValid = true         // boolean
create isOnline = false       // boolean
create nickname = empty       // empty
change nickname = "Alex"      // text
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
// Function

define add(accept a, b)
    create result = a + b
    return with result

create result = add(send 1, 2)
display(send result)
```

## OOP

*(paused feature, may be removed)*

```
// Class

describe Point
    has x
    has y

    define onNew(accept x, y)
        change me.x = x
        change me.y = y

    define move(accept x, y)
        change me.x = me.x + x
        change me.y = me.y + y

create pointA = Point(send 1, 2)
pointA.move(send 3, 4)
```

## Small Program

The program does the following:

1. When `timeCount()` is invoked, it will time how long it takes to count from `0` to `target` in increments of `step`, while displaying the current count.
2. If the arguments sent were valid, `evaluate()` is invoked to evaluate if we guessed the right number of seconds it took to count.

Assumption: Only number data types are passed.
(Data type-checking is on the roadmap.)

```
// Time visibly counting to a target
define timeCount(accept target, step)
    if target <= 0 or step <= 0
        display(send "Numbers must be positive.")
        return

    create count = 0
    create startTime = time()

    repeat while count < target
        change count = count + step
        display(send count)

    create endTime = time()

    return with (endTime - startTime) / 1000

// Evaluate a guess
define evaluate(accept guess, actual)
    if guess equals actual
        display(send "Correct.")
    otherwise if guess < actual
        display(send "Too low.")
    otherwise
        display(send "Too high.")

// Start the count
create target = 100
create step = 1
create result = timeCount(send target, step)

// Guess the number of seconds
if result unequals empty
    create guess = 0.01
    evaluate(send guess, result)
```
