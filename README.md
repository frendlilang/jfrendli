# The Frendli Programming Language

### v0.1

Frendli is an open-source general-purpose programming language designed around empirical data on difficulties and misconceptions held by novice programmers.

> ⚠️ Frendli is in active development (as are the docs).

## Table of Contents
- [Welcome](#welcome-)
- [Purpose](#purpose)
- [Goals](#goals)
- [General Characteristics](#general-characteristics)
- [Features (v0.1)](#features-v01)
- [Future Additions](#future-additions)
- [Frendli Code Snippets](#frendli-code-snippets)
    - [Variables, Data Types, and Literals](#variables-data-types-and-literals)
    - [Control Flow](#control-flow)
    - [Functions](#functions)
    - [OOP](#oop)
- [License](#license)

## Welcome 👋

Hey there!

Welcome to the `super` `Frendli` programming language for learning and teaching programming. It is still in active development, but here's a quick (and runnable) glimpse of it:

<a href="snippets/snippets.md#small-program">
  <img src="snippets/frendli-snippet-small-program.svg" width="600" alt="A Frendli code snippet.">
</a>

## Purpose

Frendli was developed to address to the frequent challenges experienced by novice students. Simply put, to create a friendlier language.

It is intended for use in the beginning stages of introductory programming courses, helping the student conceptualize and start programming with minimal friction, and thereafter transitioning early or mid-course to a different established language.

The syntactic and semantic design is based on published studies on novices in introductory programming courses at universities. The studies revealed numerous factors that can be exploited in language design to facilitate learning to program.

Frendli was originally developed as part of the thesis [Designing an Introductory Programming Language Based on Studies on Novices](https://lnu.diva-portal.org/smash/record.jsf?pid=diva2:1670920).

## Goals

Frendli is novice-oriented and centers around three main goals which are:

### 1. Be a low barrier to entry

*Subgoal: Be easy to use and understand.*

Since first-time programmers are the intended primary users of the language, lowering the barrier of entry and alleviating initial hurdles is important.

This means*:
* Producing fewer encountered difficulties and misconceptions.
* Minimizing interference of syntax and time fixing syntax errors.
* Being more intuitive and less ambiguous.
* Having consistency between syntax and semantics.
* Being less challenging for non-native English speakers.

### 2. Facilitate conceptualization

*Subgoal: Illustrate what constructs do through their syntax.*

Misconceptions are very common among novices and often arise from the syntax itself (see [article](https://lnu.diva-portal.org/smash/record.jsf?pid=diva2:1670920)). Frendli's goal is therefore to help the user conceptualize.

This means*:
* Having syntax that is more self-explanatory.

### 3. Be universally transitional

*Subgoal: Be a useful tool for teaching introductory programming and concepts.*

Allowing easier transition into multiple (i.e. universally) languages not only helps the student, but provides a realistic alternative for more educational institutions in terms of efficiently incorporating it into their existing programs, especially in combination with being useful for teaching.

This means*:
* Not forcing the use of entity terminology specific to Frendli.
* Having less syntactic and conceptual conflict with the other languages.
* Being less programming language dependent for reading and reasoning.
* Being pedagogically easier to focus on fundamental concepts.

&ast; *Compared to other languages used in introductory university courses.*

## General Characteristics

* Text-based
* High-level
* General-purpose
* Dynamically typed
* Imperative
* Optionally object-oriented
  * *(paused feature, may be removed)*
* Interpreted

## Features (v0.1)

Implemented features are marked as completed.

- [x] Data types
  - [x] `number` (`-2`, `0`, `10.5`)
  - [x] `text` (`"Hello Frendli Programmer!"`)
  - [x] `boolean` (`true`, `false`)
  - [x] `empty`
- [x] Single-line comments (`//`)
- [x] Lexical scope
- [x] Variables
  - [x] Declaration (`create`)
  - [x] Assignment (`change`)
  - [x] Dynamic typing
- [x] Operators
  - [x] Assignment
    - [x] Bind to (`=`)
  - [x] Comparison
    - [x] Less than (`<`)
    - [x] Less than or equal to (`<=`)
    - [x] Greater than (`>`)
    - [x] Greater than or equal to (`>=`)
    - [x] Equal to (`equals`)
    - [x] Not equal to (`unequals`)
  - [x] Logical
    - [x] Conjunction (`and`)
    - [x] Conjunction (`or`)
    - [x] Negation (`not`)
  - [x] Arithmetic
    - [x] Addition (`+`)
    - [x] Subtraction (`-`)
    - [x] Negation (`-`)
    - [x] Multiplication (`*`)
    - [x] Division (`/`)
  - [x] Text
    - [x] Concatenation (`+`)
  - [x] Precedence altering
    - [x] Grouping (`()`)
- [x] Control
  - [x] Selection
    - [x] `if`
      - [x] `otherwise if`
      - [x] `otherwise`
  - [x] Loop
    - [x] Bounded (`repeat times`)
    - [x] Unbounded (`repeat while`)
- [x] Functions
  - [x] Declaration and definition (`define`)
    - [x] Accept parameters (`(accept a, b, c)`)
    - [x] Return without explicit return value (`return`)
    - [x] Return with explicit return value (`return with`)
  - [x] Call a function (`()`)
    - [x] Send arguments (`(send a, b, c)`)
  - [x] Closure
- [ ] OOP *(paused feature, may be removed)*
  - [ ] Classes / user-defined types
    - [ ] Declaration and definition (`describe`)
    - [ ] Fields
      - [ ] Declaration (`has`)
    - [ ] Methods
      - [x] Declaration and definition (`define`) (same as function)
      - [ ] Constructor (`onNew`)
      - [ ] Self reference (`me`)
      - [x] Call a method (same as function)
    - [ ] Single public inheritance (`inherit`)
      - [ ] Parent reference (`parent`)
  - [ ] Instances
    - [ ] Instantiation (`new`)
    - [ ] Member (field or method) access (`.`)
- [x] Standard library
  - [x] Functions
    - [x] Output text to user (`display`)
    - [x] Get milliseconds since epoch (`time`)
- [ ] Error reporter
  - [x] Initial error messages (not yet “friendlified”)
  - [ ] Provide highly user-friendly (and novice-friendly) error messages
- [ ] REPL (interactive prompt)
  - [ ] Provide separate grammar to allow omitting the newline character

## Future Additions

#### Near to Intermediate-term

* Array/list data structure
* Extended standard library
  * I/O operations (e.g. reading keyboard input)
  * Data type checking
  * Data type casting
* Arithmetic operators (e.g. modulus)
* Control statements (e.g. terminate execution of a loop)

## Frendli Code Snippets

#### Variables, Data Types, and Literals

<a href="snippets/snippets.md#variables-data-types-and-literals">
  <img src="snippets/frendli-snippet-variables.svg" width="600" alt="A Frendli code snippet.">
</a>

#### Control Flow

<a href="snippets/snippets.md#selection">
  <img src="snippets/frendli-snippet-selection.svg" width="600" alt="A Frendli code snippet.">
</a>

<br>
<br>

<a href="snippets/snippets.md#bounded-loop">
  <img src="snippets/frendli-snippet-bounded-loop.svg" width="600" alt="A Frendli code snippet.">
</a>

<br>
<br>

<a href="snippets/snippets.md#unbounded-loop">
  <img src="snippets/frendli-snippet-unbounded-loop.svg" width="600" alt="A Frendli code snippet.">
</a>

#### Functions

<a href="snippets/snippets.md#functions">
  <img src="snippets/frendli-snippet-function.svg" width="600" alt="A Frendli code snippet.">
</a>

#### OOP

*(paused feature, may be removed)*

<a href="snippets/snippets.md#oop">
  <img src="snippets/frendli-snippet-class.svg" width="600" alt="A Frendli code snippet.">
</a>

## License

This software is licensed under the terms of the [MIT license](LICENSE).
