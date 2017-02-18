// **********************************************************************
//
// 写表客户端 client2
//
// **********************************************************************
package com.weisi.Client.frame.init;

import com.weisi.Client.service.ice.impl.RechargeClientCallbackI;

import RechargeIce.RechargeCallbackPrx;
import RechargeIce.RechargeCallbackPrxHelper;
import RechargeIce.RechargeSessionPrx;
import RechargeIce.RechargeSessionPrxHelper;

public class Client2 extends Glacier2.Application
{
    class ShutdownHook extends Thread
    {
        @Override
        public void
        run()
        {
            try
            {
                communicator().destroy();
            }
            catch(Ice.LocalException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private static void
    menu()
    {
        System.out.println(
            "usage:\n" +
            "t: send callback as twoway\n" +
            "o: send callback as oneway\n" +
            "O: send callback as batch oneway\n" +
            "f: flush all batch requests\n" +
            "v: set/reset override context field\n" +
            "F: set/reset fake category\n" +
            "s: shutdown server\n" +
            "r: restart the session\n" +
            "x: exit\n" +
            "?: help\n");
    }

    @Override
    public void sessionDestroyed()
    {
        System.out.println("The Glacier2 session has been destroyed.");
    }

    @Override
    public Glacier2.SessionPrx createSession()
    {
        Glacier2.SessionPrx session;
        while(true)
        {
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
            System.out.println("This demo accepts any user-id / password combination.");

            String id;
            String pw;
            try
            {
                System.out.print("user id: ");
                System.out.flush();
                id = in.readLine();

                System.out.print("password: ");
                System.out.flush();
                pw = in.readLine();
            }
            catch(java.io.IOException ex)
            {
                ex.printStackTrace();
                continue;
            }

            try
            {
                session = router().createSession(id, pw);
                break;
            }
            catch(Glacier2.PermissionDeniedException ex)
            {
                System.out.println("permission denied:\n" + ex.reason);
            }
            catch(Glacier2.CannotCreateSessionException ex)
            {
                System.out.println("cannot create session:\n" + ex.reason);
            }
        }
        return session;
    }

    @Override
    public int
    runWithSession(String[] args)
        throws RestartSessionException
    {
        if(args.length > 0)
        {
            System.err.println(appName() + ": too many arguments");
            return 1;
        }

        //
        // Since this is an interactive demo we want to clear the
        // Application installed interrupt callback and install our
        // own shutdown hook.
        //
        setInterruptHook(new ShutdownHook());

        try
        {
            Ice.Identity callbackReceiverIdent = createCallbackIdentity("callbackReceiver");
            Ice.Identity callbackReceiverFakeIdent = new Ice.Identity("fake", "callbackReceiver");

            objectAdapter().add(new RechargeClientCallbackI(), callbackReceiverFakeIdent);

            RechargeCallbackPrx twowayR = RechargeCallbackPrxHelper.uncheckedCast(
                objectAdapter().add(new RechargeClientCallbackI(), callbackReceiverIdent));
            RechargeCallbackPrx onewayR = RechargeCallbackPrxHelper.uncheckedCast(twowayR.ice_oneway());


            Ice.ObjectPrx base = communicator().propertyToProxy("ChatServer.Proxy");
            
            RechargeSessionPrx twoway = RechargeSessionPrxHelper.uncheckedCast(session());
            //RechargeSessionPrx twoway = RechargeSessionPrxHelper.checkedCast(base);
            
            RechargeSessionPrx oneway = RechargeSessionPrxHelper.uncheckedCast(twoway.ice_oneway());
            RechargeSessionPrx batchOneway = RechargeSessionPrxHelper.uncheckedCast(twoway.ice_batchOneway());

            
            menu();

            String line = null;
            String override = null;
            boolean fake = false;
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
            do
            {
                System.out.print("==> ");
                System.out.flush();
                try
                {
                    line = in.readLine();
                }
                catch(java.io.IOException ex)
                {
                    ex.printStackTrace();
                    line = null;
                }

                if(line == null)
                {
                    break;
                }

                if(line.equals("t"))
                {
                    java.util.Map<String, String> context = new java.util.HashMap<String, String>();
                    context.put("_fwd", "t");
                    if(override != null)
                    {
                        context.put("_ovrd", override);
                    }
                    twoway.setCallback(twowayR);
                    twoway.say("hahahahahaha");
                    //twoway.initiateCallback(twowayR, context);
                }
                else if(line.equals("o"))
                {
                    java.util.Map<String, String> context = new java.util.HashMap<String, String>();
                    context.put("_fwd", "o");
                    if(override != null)
                    {
                        context.put("_ovrd", override);
                    }
                    //oneway.initiateCallback(onewayR, context);
                }
                else if(line.equals("O"))
                {
                    java.util.Map<String, String> context = new java.util.HashMap<String, String>();
                    context.put("_fwd", "O");
                    if(override != null)
                    {
                        context.put("_ovrd", override);
                    }
                    //batchOneway.initiateCallback(onewayR, context);
                }
                else if(line.equals("f"))
                {
                    batchOneway.ice_flushBatchRequests();
                }
                else if(line.equals("v"))
                {
                    if(override == null)
                    {
                        override = "some_value";
                        System.out.println("override context field is now `" + override + "'");
                    }
                    else
                    {
                        override = null;
                        System.out.println("override context field is empty");
                    }
                }
                else if(line.equals("F"))
                {
                    fake = !fake;

                    if(fake)
                    {
                        twowayR = RechargeCallbackPrxHelper.uncheckedCast(
                            twowayR.ice_identity(callbackReceiverFakeIdent));
                        onewayR = RechargeCallbackPrxHelper.uncheckedCast(
                            onewayR.ice_identity(callbackReceiverFakeIdent));
                    }
                    else
                    {
                        twowayR = RechargeCallbackPrxHelper.uncheckedCast(
                            twowayR.ice_identity(callbackReceiverIdent));
                        onewayR = RechargeCallbackPrxHelper.uncheckedCast(
                            onewayR.ice_identity(callbackReceiverIdent));
                    }

                    System.out.println("callback receiver identity: " +
                                    communicator().identityToString(twowayR.ice_getIdentity()));
                }
                else if(line.equals("s"))
                {
                    //twoway.shutdown();
                }
                else if(line.equals("r"))
                {
                    restart();
                }
                else if(line.equals("x"))
                {
                    // Nothing to do
                }
                else if(line.equals("?"))
                {
                    menu();
                }
                else
                {
                    System.out.println("unknown command `" + line + "'");
                    menu();
                }
            }
            while(!line.equals("x"));
        }
        catch(Glacier2.SessionNotExistException ex)
        {
            System.err.println(appName() + ": " + ex.toString());
            return 1;
        }

        return 0;
    }

    public static void
    main(String[] args)
    {
        Client2 app = new Client2();
        int status = app.main("Client", args, "config.client");
        System.exit(status);
    }
}