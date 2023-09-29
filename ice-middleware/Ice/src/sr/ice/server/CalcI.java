package sr.ice.server;
// **********************************************************************
//
// Copyright (c) 2003-2019 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

import Demo.InvalidInput;
import Demo.ZeroDivisionError;
import com.zeroc.Ice.Current;

import Demo.A;
import Demo.Calc;

import java.util.Map;

public class CalcI implements Calc
{
	private static final long serialVersionUID = -2448962912780867770L;
	long counter = 0;

	@Override
	public double calculateLagrange(int x, Map<Integer, int[]> coefficients, Current __current)
			throws ZeroDivisionError, InvalidInput {
		if (coefficients.get(0) == null || coefficients.get(1) == null ||
			coefficients.get(0).length != coefficients.get(1).length)
			throw new InvalidInput();
		double total = 0;
		for (int j=0; j < coefficients.get(0).length; j++) {
			double value = 1;
			for (int i = 0; i < coefficients.get(0).length; i++) {
				if (i == j) continue;
				try {
					value *= (double) (x - coefficients.get(0)[i]) /
							(coefficients.get(0)[j] - coefficients.get(1)[i]);
				} catch (ArithmeticException e) {
					System.out.println("Zero division");
					throw new ZeroDivisionError();
				}
			}
			System.out.println("Adding " + value);
			total += value;
		}

		return total;
	}

	@Override
	public long add(int a, int b, Current __current) 
	{
		System.out.println("ADD: a = " + a + ", b = " + b + ", result = " + (a+b));
		
		if(a > 1000 || b > 1000) {
			try { Thread.sleep(6000); } catch(java.lang.InterruptedException ex) { } 
		}
		
		if(__current.ctx.values().size() > 0) System.out.println("There are some properties in the context");

		System.out.println(__current.id);
		return a + b;
	}
	
	@Override
	public long subtract(int a, int b, Current __current) {
		System.out.println("SUBTRACT: a = " + a + ", b = " + b + ", result = " + (a-b));

		return a - b;
	}


	@Override
	public /*synchronized*/ void op(A a1, short b1, Current current) {
		System.out.println("OP" + (++counter));
		try { Thread.sleep(500); } catch(java.lang.InterruptedException ex) { }		
	}

	@Override
	public double avg(long[] a, Current current) {
		long sum = 0;
		for (long l : a) sum += l;
		return (double) sum / a.length;
	}
}
