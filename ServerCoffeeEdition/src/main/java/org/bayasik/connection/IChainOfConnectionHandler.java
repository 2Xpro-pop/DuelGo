package org.bayasik.connection;

public interface IChainOfConnectionHandler {
    void accept(ConnectionContext context, IChainOfConnectionHandler next);
    public class NextInvoker implements IChainOfConnectionHandler
    {
        private int index = 0;
        private final IChainOfConnectionHandler[] chains;

        public NextInvoker(IChainOfConnectionHandler[] chains) {
            this.chains = chains;
        }

        @Override
        public void accept(ConnectionContext context, IChainOfConnectionHandler next) {
           if (index < chains.length) {
             chains[index++].accept(context, this);
           }
        }
    }
}
