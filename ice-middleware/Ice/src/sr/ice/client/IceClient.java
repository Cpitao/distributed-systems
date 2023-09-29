package sr.ice.client;
// **********************************************************************
//
// Copyright (c) 2003-2019 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

import Demo.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.LocalException;

public class IceClient 
{
	public static void main(String[] args) 
	{
		int status = 0;
		Communicator communicator = null;

		try {
			// 1. Inicjalizacja ICE
			communicator = Util.initialize(args);

			// 2. Uzyskanie referencji obiektu na podstawie linii w pliku konfiguracyjnym (wówczas aplikację należy uruchomić z argumentem --Ice.config=config.client)
			//ObjectPrx base1 = communicator.propertyToProxy("Calc1.Proxy");
			
			// 2. To samo co powyżej, ale mniej ładnie
			ObjectPrx base1 = communicator.stringToProxy("calc/calc11:tcp -h 127.0.0.2 -p 10000 -z : udp -h 127.0.0.2 -p 10000 -z"); //opcja -z włącza możliwość kompresji wiadomości
			ObjectPrx base2 = communicator.stringToProxy("calc/calc22:tcp -h 127.0.0.2 -p 10000 -z : udp -h 127.0.0.2 -p 10000 -z"); //opcja -z włącza możliwość kompresji wiadomości
			ObjectPrx base3 = communicator.stringToProxy("calc/calc33:tcp -h 127.0.0.2 -p 10000 -z : udp -h 127.0.0.2 -p 10000 -z"); //opcja -z włącza możliwość kompresji wiadomości

			// 3. Rzutowanie, zawężanie (do typu Calc)
			CalcPrx obj1 = CalcPrx.checkedCast(base1);
			CalcPrx obj2 = CalcPrx.checkedCast(base2);
			CalcPrx obj3 = CalcPrx.checkedCast(base3);
			if (obj1 == null) throw new Error("Invalid proxy");
			
			CompletableFuture<Long> cfl = null;
			String line = null;
			java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

			// 4. Wywołanie zdalnych operacji i zmiana trybu proxy dla obiektu obj1
			int iter=0;
			do	{
				try	{
					System.out.print("==> ");
					line = in.readLine();
					
					if (line == null)
					{
						break;
					}
					else if (line.equals("add"))
					{
						iter = (iter+1) % 3;
						long r=0;
						switch(iter) {
							case 0: r = obj1.add(7, 8);
							case 1: r = obj2.add(7, 8);
							case 2: r = obj3.add(7, 8);
						}

						System.out.println("RESULT = " + r);
					}
					else if (line.equals("avg"))
					{
						double r;
						long[] nums = {1, 2, 3, 4, 5};
						r = obj1.avg(nums);
						System.out.println("RESULT = " + r);
					}
					else if (line.equals("add2")) 
					{
						long r = obj1.add(7000, 8000);
						System.out.println("RESULT = " + r);
					}
					else if (line.equals("subtract"))
					{
						long r = obj1.subtract(7, 8);
						System.out.println("RESULT = " + r);
					}
					else if (line.equals("op"))
					{
						A a = new A((short)11, 22, 33.0f, "ala ma kota");
						obj1.op(a, (short)44);
						System.out.println("DONE");
					} 
					else if (line.equals("op2"))
					{
						A a = new A((short)11, 22, 33.0f, "ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ala ma kota ");
						obj1.op(a, (short)44);
						System.out.println("DONE");
					} 
					else if (line.equals("op 10"))
					{
						A a = new A((short)11, 22, 33.0f, "ala ma kota");
						for(int i = 0; i < 10; i++) obj1.op(a, (short)44);
						System.out.println("DONE");
					}
					else if (line.equals("add-with-ctx")) //wysłanie dodatkowych danych stanowiących kontekst wywołania
					{
						Map<String,String> map = new HashMap<String, String>();
						map.put("key1", "val1");
						map.put("key2", "val2");
						long r = obj1.add(7, 8, map);
						System.out.println("RESULT = " + r);
					}
					else if (line.equals("compress on"))  
					{
						obj1.ice_compress(true);
						System.out.println("Compression enabled");
					} 
					else if (line.equals("compress off"))  
					{
						obj1.ice_compress(false);
						System.out.println("Compression disabled");
					} 

					/* PONIŻEJ WYWOŁANIA REALIZOWANE W TRYBIE ASYNCHRONICZNYM */
					
					else if (line.equals("add-asyn1")) //future-based
					{
						obj1.addAsync(7000, 8000).whenComplete((result, ex) -> 
							{
								System.out.println("RESULT (asyn) = " + result);
							}
						);
					}
					else if (line.equals("add-asyn2-req")) //future-based  1. zlecenie wyłania
					{
						cfl = obj1.addAsync(7000, 8000);						
					}
					else if (line.equals("add-asyn2-res")) //future-based  2. odebranie wyniku
					{
						long r = cfl.join();						
						System.out.println("RESULT = " + r);
					}
					else if (line.equals("op-asyn1a 100")) //co się dzieje "w sieci"?
					{
						A a = new A((short)11, 22, 33.0f, "ala ma kota");
						for(int i = 0; i <100; i++) 
						{
							obj1.opAsync(a, (short)99);
						}
						System.out.println("DONE");
					}
					else if (line.equals("op-asyn1b 100")) 
					{
						A a = new A((short)11, 22, 33.0f, "ala ma kota");
						for(int i = 0; i <100; i++) 
						{
							obj1.opAsync(a, (short)99).whenComplete((result, ex) -> 
								{
									System.out.println("CALL (asyn) finished");
								}
							);
						}
						System.out.println("DONE");
					}
					
					/* PONIŻEJ USTAWIANIE TRYBU PRACY PROXY */
					
					else if (line.equals("set-proxy twoway")) 
					{
						obj1 = obj1.ice_twoway();
						System.out.println("obj1 proxy set to 'twoway' mode");
					}
					else if (line.equals("set-proxy oneway")) 
					{
						obj1 = obj1.ice_oneway();
						System.out.println("obj1 proxy set to 'oneway' mode");
					}
					else if (line.equals("set-proxy datagram")) 
					{
						obj1 = obj1.ice_datagram();
						System.out.println("obj1 proxy set to 'datagram' mode");
					}
					else if (line.equals("set-proxy batch oneway")) 
					{
						obj1 = obj1.ice_batchOneway();
						System.out.println("obj1 proxy set to 'batch oneway' mode");
					}
					else if (line.equals("set-proxy batch datagram")) 
					{
						obj1 = obj1.ice_batchDatagram();
						System.out.println("obj1 proxy set to 'batch datagram' mode");
					}
					else if (line.equals("flush")) //sensowne tylko dla operacji wywoływanych w trybie batch
					{
						obj1.ice_flushBatchRequests();
						System.out.println("Flush DONE");
					} 
					else if (line.equals("x") || line.equals("")) // exit
					{
					}
					else
					{
						System.out.println("???");
					}
				}
				catch (java.io.IOException ex)
				{
					System.err.println(ex);
				}
				catch (com.zeroc.Ice.TwowayOnlyException ex)
				{
					System.err.println(ex);
				}
			}
			while (!line.equals("x"));


		} catch (LocalException e) {
			e.printStackTrace();
			status = 1;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			status = 1;
		}
		if (communicator != null) { //clean
			try {
				communicator.destroy();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				status = 1;
			}
		}
		System.exit(status);
	}

}