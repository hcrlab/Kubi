# KubiLingo app
This is an Android app. If you are not familiar with Android development, the Android Developer [Tutorials](https://developer.android.com/training/index.html) should be helpful.

## Overview
* [Frontend](app/src/main/java/uw/hcrlab/kubi/robot) - controls for the robot base, animated hands, animated eyes, and speech. Calls into [Kubi API](app/src/main/java/com/revolverobotics/kubiapi) which implements much of the functionality.
* [Lesson Stuff](app/src/main/java/uw/hcrlab/kubi/lesson) - for asking the user questions and showing them if the answer was right or wrong

## See also
* [KubiLingo Paper](https://homes.cs.washington.edu/~lrperlmu/perlmutter16icsr.pdf) (ICSR 2016)
* [Kubilingo Demo Video](https://www.youtube.com/watch?v=3ByEiixPTWk)
* [kubi-duo-adapter](https://gitlab.cs.washington.edu/hcrlab/kubi-duo-adapter) and [kubiwoz2](https://gitlab.cs.washington.edu/hcrlab/kubiwoz2) contain additional code used to implement the app described in the paper.



