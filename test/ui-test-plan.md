# Zikiai UI Test Plan

The UI test runner starts a fresh chatbot process for each case. Commands are supplied in order, one per line. Expected output is matched exactly after expanding these placeholders:

- `{{SEPARATOR}}`: 60 underscore characters
- `{{BANNER}}`: the five-line Zikiai banner

A case can include an optional `Initial data file` block to seed `data/zikiai.txt` before startup. It can also include an optional `Expected data file` block, which the runner compares with the file after the commands finish.

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

**Aim:** Verify that an ISO deadline date is parsed, displayed in a friendly format, stored, marked as done, and listed correctly.

### Input

```text
deadline return book /by 2026-08-23
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
    [D][ ] return book (by: Aug 23 2026)
Now you have 1 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Nice! I've marked this task as done:
    [D][X] return book (by: Aug 23 2026)
{{SEPARATOR}}
{{SEPARATOR}}
Here are the tasks in your list:
1.[D][X] return book (by: Aug 23 2026)
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
deadline return book /by 2026-08-23
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
    [D][ ] return book (by: Aug 23 2026)
Now you have 2 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Noted. I've removed this task:
    [D][ ] return book (by: Aug 23 2026)
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

## TC-11 Save the latest task state to disk

**Aim:** Verify that adding, marking, and deleting tasks rewrites the data file with the latest task types, statuses, and details.

### Input

```text
todo read book
deadline return book /by 2026-08-23
event project meeting /from Mon 2pm /to 4pm
mark 1
delete 2
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
    [D][ ] return book (by: Aug 23 2026)
Now you have 2 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Nice! I've marked this task as done:
    [T][X] read book
{{SEPARATOR}}
{{SEPARATOR}}
Noted. I've removed this task:
    [D][ ] return book (by: Aug 23 2026)
Now you have 2 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
okay, bai bai
{{SEPARATOR}}
```

### Expected data file

```text
[T][X] | read book
[E][ ] | project meeting | Mon 2pm | 4pm
```

## TC-12 Save an empty list after deleting the last task

**Aim:** Verify that deleting the only task truncates the data file instead of leaving stale task data behind.

### Input

```text
todo temporary task
delete 1
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
    [T][ ] temporary task
Now you have 1 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Noted. I've removed this task:
    [T][ ] temporary task
Now you have 0 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
okay, bai bai
{{SEPARATOR}}
```

### Expected data file

```text

```

## TC-13 Load tasks and preserve their state

**Aim:** Verify that startup reconstructs all task types and done statuses, and that loaded tasks can still be unmarked and deleted correctly.

### Initial data file

```text
[T][X] | read book
[D][ ] | return book | 2026-08-23
[E][X] | project meeting | Mon 2pm | 4pm
```

### Input

```text
list
unmark 1
delete 2
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
1.[T][X] read book
2.[D][ ] return book (by: Aug 23 2026)
3.[E][X] project meeting (from: Mon 2pm to: 4pm)
{{SEPARATOR}}
{{SEPARATOR}}
OK, I've marked this task as not done yet:
    [T][ ] read book
{{SEPARATOR}}
{{SEPARATOR}}
Noted. I've removed this task:
    [D][ ] return book (by: Aug 23 2026)
Now you have 2 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Here are the tasks in your list:
1.[T][ ] read book
2.[E][X] project meeting (from: Mon 2pm to: 4pm)
{{SEPARATOR}}
{{SEPARATOR}}
okay, bai bai
{{SEPARATOR}}
```

### Expected data file

```text
[T][ ] | read book
[E][X] | project meeting | Mon 2pm | 4pm
```

## TC-14 Reject an unknown saved task type

**Aim:** Verify that an unknown task type is reported safely and the chatbot exits before overwriting the corrupted file.

### Initial data file

```text
[Z][ ] | mysterious task
```

### Input

```text
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
OOPSSSIES!!! I couldn't load the saved tasks because line 1 is invalid.
{{SEPARATOR}}
```

### Expected data file

```text
[Z][ ] | mysterious task
```

## TC-15 Reject malformed saved fields

**Aim:** Verify that a missing deadline field is detected instead of constructing a partial task.

### Initial data file

```text
[T][ ] | valid task
[D][ ] | return book
```

### Input

```text
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
OOPSSSIES!!! I couldn't load the saved tasks because line 2 is invalid.
{{SEPARATOR}}
```

### Expected data file

```text
[T][ ] | valid task
[D][ ] | return book
```

## TC-16 Reject an invalid saved status

**Aim:** Verify that only `[X]` and `[ ]` completion statuses are accepted from storage.

### Initial data file

```text
[T][?] | uncertain task
```

### Input

```text
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
OOPSSSIES!!! I couldn't load the saved tasks because line 1 is invalid.
{{SEPARATOR}}
```

### Expected data file

```text
[T][?] | uncertain task
```

## TC-17 Reject reserved separators without changing state

**Aim:** Verify that pipe characters in new task fields are rejected before they can create ambiguous saved data, while valid stored tasks remain unchanged.

### Input

```text
todo keep me
todo bad | task
deadline return | book /by Sunday
event meeting /from Mon | noon /to 4pm
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
OOPSSSIES!!! Task details cannot contain the | character.
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! Task details cannot contain the | character.
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! Task details cannot contain the | character.
{{SEPARATOR}}
{{SEPARATOR}}
Here are the tasks in your list:
1.[T][ ] keep me
{{SEPARATOR}}
{{SEPARATOR}}
okay, bai bai
{{SEPARATOR}}
```

### Expected data file

```text
[T][ ] | keep me
```

## TC-18 Load an empty data file

**Aim:** Verify that an existing empty data file behaves like an empty task list and does not cause a startup error.

### Initial data file

```text

```

### Input

```text
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
okay, bai bai
{{SEPARATOR}}
```

### Expected data file

```text

```

## TC-19 Validate deadline dates without changing existing tasks

**Aim:** Interleave valid and invalid deadline commands to verify that non-ISO and impossible dates are rejected without changing the list, while a valid leap-day deadline is accepted.

### Input

```text
todo keep me
deadline words /by Sunday
deadline impossible /by 2025-02-29
list
deadline leap day /by 2024-02-29
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
OOPSSSIES!!! Please enter the deadline as yyyy-MM-dd, for example 2026-08-23.
{{SEPARATOR}}
{{SEPARATOR}}
OOPSSSIES!!! Please enter the deadline as yyyy-MM-dd, for example 2026-08-23.
{{SEPARATOR}}
{{SEPARATOR}}
Here are the tasks in your list:
1.[T][ ] keep me
{{SEPARATOR}}
{{SEPARATOR}}
Got it. I've added this task:
    [D][ ] leap day (by: Feb 29 2024)
Now you have 2 tasks in the list.
{{SEPARATOR}}
{{SEPARATOR}}
Here are the tasks in your list:
1.[T][ ] keep me
2.[D][ ] leap day (by: Feb 29 2024)
{{SEPARATOR}}
{{SEPARATOR}}
okay, bai bai
{{SEPARATOR}}
```

### Expected data file

```text
[T][ ] | keep me
[D][ ] | leap day | 2024-02-29
```

## TC-20 Reject an impossible date in saved data

**Aim:** Verify that an impossible saved deadline date is reported as corrupted data and is not overwritten.

### Initial data file

```text
[D][ ] | impossible task | 2025-02-29
```

### Input

```text
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
OOPSSSIES!!! I couldn't load the saved tasks because line 1 is invalid.
{{SEPARATOR}}
```

### Expected data file

```text
[D][ ] | impossible task | 2025-02-29
```
