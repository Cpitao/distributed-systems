
#ifndef CALC_ICE
#define CALC_ICE

module Demo
{
  enum operation { MIN, MAX, AVG };
  
  exception NoInput {};

  sequence<long> LongSeq;

  struct A
  {
    short a;
    long b;
    float c;
    string d;
  }

  interface Calc
  {
    long add(int a, int b);
    long subtract(int a, int b);
    void op(A a1, short b1); //za?ó?my, ?e to te? jest operacja arytmetyczna ;)
    double avg(LongSeq a);
  };

};

#endif
