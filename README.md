# allentemporalrelationships
Implementation of Allen's temporal interval relationships and the path consistency algorithm in Java

Allen proposed in his paper thirteen different relationships/constraints (before,after,meets,met by,overlaps, overlapped by, starts, started by, finishes, finished by, contains, during, equals) between temporal intervals (e.g. activities). He also describes algorithms for reasoning about them (e.g. deriving all possible relationships from a given set of relationships between activities or detecting inconsistent temporal interval relationships).

Reasoning on temporal interval relationships / constraints is highly relevant in practice for process management, project management, temporal data etc.

This project implements a java library for representing and reasoning on Allen's proposed temporal interval relationships.

If you use this source code or the documentation (e.g in your software or in teaching material), please do not forget to send me an e-mail (see source code). I will keep this information confidential if you want.

References:

* Allen, J. F. Maintaining Knowledge about Temporal Intervals Communications of the ACM, 1983, 26, 832-843

* Nebel, B. & Bürckert, H.-J. Reasoning about Temporal Relations: A Maximal Tractable Subclass of Allen's Interval Algebra, Journal of the ACM, 1995, 42, 43-66 
