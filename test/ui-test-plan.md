# Zikiai UI Test Plan

The UI test runner starts a fresh chatbot process for each case. Commands are supplied in order, one per line. Expected output is matched exactly after expanding these placeholders:

- `{{SEPARATOR}}`: 60 underscore characters
- `{{BANNER}}`: the five-line Zikiai banner

## TC-01 Add and list a todo

**Aim:** Verify that a todo is stored, displayed with the todo type and incomplete status, and included in the task count.

### Input

```text
todo borrow book
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai.
What can I do for you?
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [T][ ] borrow book
Now you have 1 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Here are the tasks in your list:
1.[T][ ] borrow book
{{SEPARATOR}}
{{SEPARATOR}}
okay, bai bai
{{SEPARATOR}}
```

## TC-02 Add and mark a deadline

**Aim:** Verify that a deadline is dissected around `/by`, stored, marked as done, and listed with its deadline.

### Input

```text
deadline return book /by Sunday
mark 1
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai.
What can I do for you?
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [D][ ] return book (by: Sunday)
Now you have 1 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Nice! I've marked this task as done:
    [D][X] return book (by: Sunday)
{{SEPARATOR}}
{{SEPARATOR}}
Here are the tasks in your list:
1.[D][X] return book (by: Sunday)
{{SEPARATOR}}
{{SEPARATOR}}
okay, bai bai
{{SEPARATOR}}
```

## TC-03 Add and unmark an event

**Aim:** Verify that an event is dissected around `/from` and `/to`, and that its inherited done status can be reversed.

### Input

```text
event project meeting /from Mon 2pm /to 4pm
mark 1
unmark 1
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai.
What can I do for you?
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Nice! I've marked this task as done:
    [E][X] project meeting (from: Mon 2pm to: 4pm)
{{SEPARATOR}}
{{SEPARATOR}}
OK, I've marked this task as not done yet:
    [E][ ] project meeting (from: Mon 2pm to: 4pm)
{{SEPARATOR}}
{{SEPARATOR}}
Here are the tasks in your list:
1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
{{SEPARATOR}}
{{SEPARATOR}}
okay, bai bai
{{SEPARATOR}}
```

## TC-04 Handle invalid commands with exceptions

**Aim:** Verify that a missing todo description and an unknown command are reported without terminating the chatbot.

### Input

```text
todo
blah
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai.
What can I do for you?
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! The description of a todo cannot be empty.
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! I'm sorrieeee, but I don't know what that means :-(
{{SEPARATOR}}
{{SEPARATOR}}
okay, bai bai
{{SEPARATOR}}
```

## TC-05 Handle invalid task numbers with exceptions

**Aim:** Verify that mark and unmark commands report task numbers that do not exist and continue accepting commands.

### Input

```text
todo read book
mark 99
unmark 0
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai.
What can I do for you?
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [T][ ] read book
Now you have 1 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! That task number does not exist.
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! That task number does not exist.
{{SEPARATOR}}
{{SEPARATOR}}
Here are the tasks in your list:
1.[T][ ] read book
{{SEPARATOR}}
{{SEPARATOR}}
okay, bai bai
{{SEPARATOR}}
```

## TC-06 Delete a task and handle invalid deletion

**Aim:** Verify that deleting a task removes the selected list item, displays it, updates the count, and rejects a nonexistent task number.

### Input

```text
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
delete 2
list
delete 0
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai.
What can I do for you?
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [T][ ] read book
Now you have 1 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Noted. I've removed this task:
    [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Here are the tasks in your list:
1.[T][ ] read book
2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! That task number does not exist.
{{SEPARATOR}}
{{SEPARATOR}}
okay, bai bai
{{SEPARATOR}}
```

## TC-07 Reject malformed task creation without changing state

**Aim:** Verify that malformed deadline and event commands do not add partial tasks or alter a previously stored todo.

### Input

```text
todo keep me
deadline return book
deadline /by Sunday
event meeting /from Monday
event /from Monday /to Tuesday
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai.
What can I do for you?
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [T][ ] keep me
Now you have 1 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! Please specify a deadline using /by.
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! Please provide both a task and a deadline.
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! Please specify an event using /from and /to.
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! Please provide an event, a start time, and an end time.
{{SEPARATOR}}
{{SEPARATOR}}
Here are the tasks in your list:
1.[T][ ] keep me
{{SEPARATOR}}
{{SEPARATOR}}
okay, bai bai
{{SEPARATOR}}
```

## TC-08 Interleave malformed and valid status commands

**Aim:** Verify that malformed action commands do not change a task and that later valid mark and unmark commands still work.

### Input

```text
todo alpha
mark homework
mark 1
unmark 1 task
unmark 1
delete two
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai.
What can I do for you?
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [T][ ] alpha
Now you have 1 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! I'm sorrieeee, but I don't know what that means :-(
{{SEPARATOR}}
{{SEPARATOR}}
Nice! I've marked this task as done:
    [T][X] alpha
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! I'm sorrieeee, but I don't know what that means :-(
{{SEPARATOR}}
{{SEPARATOR}}
OK, I've marked this task as not done yet:
    [T][ ] alpha
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! I'm sorrieeee, but I don't know what that means :-(
{{SEPARATOR}}
{{SEPARATOR}}
Here are the tasks in your list:
1.[T][ ] alpha
{{SEPARATOR}}
{{SEPARATOR}}
okay, bai bai
{{SEPARATOR}}
```

## TC-09 Preserve numbering after deletion

**Aim:** Verify that deleting a middle task renumbers the remaining tasks and that subsequent commands use the new numbering.

### Input

```text
todo first
todo second
todo third
delete 2
mark 2
delete 3
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai.
What can I do for you?
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [T][ ] first
Now you have 1 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [T][ ] second
Now you have 2 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [T][ ] third
Now you have 3 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Noted. I've removed this task:
    [T][ ] second
Now you have 2 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Nice! I've marked this task as done:
    [T][X] third
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! That task number does not exist.
{{SEPARATOR}}
{{SEPARATOR}}
Here are the tasks in your list:
1.[T][ ] first
2.[T][X] third
{{SEPARATOR}}
{{SEPARATOR}}
okay, bai bai
{{SEPARATOR}}
```

## TC-10 Handle empty lists and oversized task numbers

**Aim:** Verify that operations on an empty list and task numbers larger than an integer are rejected without corrupting subsequently stored tasks.

### Input

```text
list
mark 1
unmark 1
delete 1
todo saved task
mark 999999999999999999999
delete 999999999999999999999
list
bye
```

### Expected output

```text
{{SEPARATOR}}
{{BANNER}}
Hello! I'm Zikiai.
What can I do for you?
{{SEPARATOR}}
{{SEPARATOR}}
Here are the tasks in your list:
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! That task number does not exist.
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! That task number does not exist.
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! That task number does not exist.
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [T][ ] saved task
Now you have 1 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! That task number is too large.
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! That task number is too large.
{{SEPARATOR}}
{{SEPARATOR}}
Here are the tasks in your list:
1.[T][ ] saved task
{{SEPARATOR}}
{{SEPARATOR}}
okay, bai bai
{{SEPARATOR}}
```
