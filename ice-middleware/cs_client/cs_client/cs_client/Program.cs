using Ice;
using IceInternal;
using System;

class Program
{
    static void Main(string[] args)
    {
        runExample1();
        runExample2();
        runExample3();
    }

    static void runExample1()
    {
        Communicator communicator = Ice.Util.initialize();
        ObjectPrx o = communicator.stringToProxy("calc/calc11:tcp -h 127.0.0.2 -p 10000 -z : udp -h 127.0.0.2 -p 10000 -z");
        try
        {
            OutputStream outStream = new Ice.OutputStream(communicator);
            outStream.startEncapsulation();
            Console.Write("Enter operation (add/subtract): ");
            String op = Console.ReadLine();
            int x, y;
            Console.Write("> ");
            x = int.Parse(Console.ReadLine());
            Console.Write("> ");
            y = int.Parse(Console.ReadLine());

            outStream.writeInt(x);
            outStream.writeInt(y);
            outStream.endEncapsulation();
            byte[] inParams = outStream.finished();

            byte[] outParams;

            if (o.ice_invoke(op, OperationMode.Normal, inParams, out outParams))
            {
                Ice.InputStream inStream = new Ice.InputStream(communicator, outParams);
                inStream.startEncapsulation();
                long result = inStream.readLong();
                inStream.endEncapsulation();
                Console.WriteLine(result);
            }
            else
            {
                Console.WriteLine("User exception");
            }
        }
        catch (LocalException e)
        {
            Console.Error.WriteLine(e);
        }
        finally
        {
            if (communicator != null)
            {
                try
                {
                    communicator.destroy();
                } catch (Ice.Exception e)
                {
                    Console.Error.WriteLine(e);
                }
            }
        }
    }

    static void runExample2()
    {
        Communicator communicator = Ice.Util.initialize();
        ObjectPrx o = communicator.stringToProxy("calc/calc11:tcp -h 127.0.0.2 -p 10000 -z : udp -h 127.0.0.2 -p 10000 -z");
        try
        {
            OutputStream outStream = new Ice.OutputStream(communicator);
            outStream.startEncapsulation();
            long[] longs = new long[100];
            Console.WriteLine("Enter up to 100 integers. End with empty line.");
            int ints = 0;
            for (int i=0; i < 100; i++)
            {
                ints = i;
                Console.Write("> ");
                String input = Console.ReadLine();
                if (input == "") break;
                longs[i] = long.Parse(input);
            }
            outStream.writeLongSeq(longs.Take(ints).ToArray());

            outStream.endEncapsulation();
            byte[] inParams = outStream.finished();

            byte[] outParams;

            if (o.ice_invoke("avg", OperationMode.Normal, inParams, out outParams))
            {
                Ice.InputStream inStream = new Ice.InputStream(communicator, outParams);
                inStream.startEncapsulation();
                double result = inStream.readDouble();
                inStream.endEncapsulation();
                Console.WriteLine(result);
            }
            else
            {
                Console.WriteLine("User exception");
            }
        }
        catch (LocalException e)
        {
            Console.Error.WriteLine(e.ToString());
        }
        finally
        {
            if (communicator != null)
            {
                try
                {
                    communicator.destroy();
                }
                catch (Ice.Exception e)
                {
                    Console.Error.WriteLine(e);
                }
            }
        }

    }

    static void runExample3()
    {
        Communicator communicator = Ice.Util.initialize();
        ObjectPrx o = communicator.stringToProxy("calc/calc11:tcp -h 127.0.0.2 -p 10000 -z : udp -h 127.0.0.2 -p 10000 -z");
        try
        {
            OutputStream outStream = new Ice.OutputStream(communicator);
            outStream.startEncapsulation();
            int x;
            Dictionary<int, int[]> coefficients = new Dictionary<int, int[]>();

            int[] nominators = new int[10];
            int[] denominators = new int[10];

            Console.WriteLine("Enter Lagrange polynomial nominator coefficients (empty to end):");
            int ints1 = 0;
            for (int i = 0; i < 10; i++)
            {
                ints1 = i;
                String value = Console.ReadLine();
                if (value == "") break;
                nominators[i] = int.Parse(value);
            }

            Console.WriteLine("Now enter denominator coefficients:");
            int ints2 = 0;
            for (int i=0; i < 10; i++)
            {
                ints2 = i;
                String value = Console.ReadLine();
                if (value == "") break;
                denominators[i] = int.Parse(value);
            }

            Console.Write("x: ");
            x = int.Parse(Console.ReadLine());
            coefficients.Add(0, nominators.Take(ints1).ToArray());
            coefficients.Add(1, denominators.Take(ints2).ToArray());

            outStream.writeInt(x);
            outStream.writeSize(coefficients.Count());
            foreach (var pair in coefficients)
            {
                outStream.writeInt(pair.Key);
                outStream.writeSize(pair.Value.Count());
                foreach (int value in pair.Value)
                {
                    outStream.writeInt(value);
                }
            }
            outStream.endEncapsulation();

            byte[] inParams = outStream.finished();
            byte[] outParams;

            if (o.ice_invoke("calculateLagrange", OperationMode.Normal, inParams, out outParams))
            {
                Ice.InputStream inStream = new Ice.InputStream(communicator, outParams);
                inStream.startEncapsulation();
                double result = inStream.readDouble();
                inStream.endEncapsulation();
                Console.WriteLine(result);
            }
            else
            {
                Console.WriteLine("User exception");
            }
        }
        catch (LocalException e)
        {
            Console.Error.WriteLine(e.ToString());
        }
        finally
        {
            if (communicator != null)
            {
                try
                {
                    communicator.destroy();
                }
                catch (Ice.Exception e)
                {
                    Console.Error.WriteLine(e);
                }
            }
        }

    }
}