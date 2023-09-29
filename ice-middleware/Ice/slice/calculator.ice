
#ifndef CALC_ICE
#define CALC_ICE

module Demo
{
  enum operation { MIN, MAX, AVG };
  
  exception NoInput {};
  exception ZeroDivisionError {};
  exception InvalidInput {};

  sequence<long> LongSeq;
  sequence<int> IntSeq;
  dictionary<int, IntSeq> Coefficients;

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
    double calculateLagrange(int x, Coefficients c) throws ZeroDivisionError, InvalidInput;
  };

};

#endif
