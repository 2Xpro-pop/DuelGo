package org.bayasik.connection;

public interface IChainOfConnectionAccept {
    void accept(ConnectionContext context, IChainOfConnectionAccept next);

   public class NextInvoker implements  IChainOfConnectionAccept
   {
       private int index = 0;
       private final IChainOfConnectionAccept[] chains;

       public NextInvoker(IChainOfConnectionAccept[] chains) {
           this.chains = chains;
       }

       @Override
       public void accept(ConnectionContext context, IChainOfConnectionAccept next) {
              if (index < chains.length) {
                chains[index++].accept(context, this);
              }
       }
   }
}
