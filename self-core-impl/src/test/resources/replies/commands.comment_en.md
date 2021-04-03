Hi @%s! Here are the commands which I understand:

* ``Hello`` -- any comment addressed to the PM containing the word **"hello"**.
    
    Equivalent command: ``Hi``
* ``Status`` -- any comment addressed to the PM, containing the word **"status"**. The PM will respond with the Task's status, assignee, deadline etc.
* ``Resign`` -- a task's assignee can always resign from the task, if they cannot or simply do not want to solve it. The comment should be addressed to the PM and contain the word **"resign"**. Only the task's assignee can give this command.
    
    Equivalent commands: ``Refuse``, ``Quit``
* ``Deregister`` -- a task can be deregistered (taken out of scope) with a comment containing the word **"deregister"**. Only a Contributor with role ``PO`` or ``ARCH`` can give this command. When a task is deregistered, the task's assignee is automatically resigned.

    Equivalent command: ``Remove``
* ``Register`` -- an Issue or PR can be registered as a Task with a comment containing the word **"register"**. Any Contributor can give this command.
    
    Equivalent command: ``Add``