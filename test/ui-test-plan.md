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
