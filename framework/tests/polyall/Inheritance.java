import polyall.quals.*;

// :: error: (super.invocation.invalid)
@H1S1 class Inheritance {
    void bar1(@H1Bot Inheritance param) {}

    void bar2(@H1S1 Inheritance param) {}
    // :: error: (type.invalid.annotations.on.use)
    void bar3(@H1Top Inheritance param) {}

    void foo1(@H1Bot Inheritance[] param) {}

    void foo2(@H1S1 Inheritance[] param) {}
    // :: error: (type.invalid.annotations.on.use)
    void foo3(@H1Top Inheritance[] param) {}
}
