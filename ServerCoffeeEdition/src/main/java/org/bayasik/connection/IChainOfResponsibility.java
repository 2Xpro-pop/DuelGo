package org.bayasik.connection;

public interface IChainOfResponsibility {
    void accept(ConnectionContext context, IChainOfResponsibility next);
    public class NextInvoker implements IChainOfResponsibility
    {
        private int index = 0;
        private final IChainOfResponsibility[] chains;

        public NextInvoker(IChainOfResponsibility[] chains) {
            this.chains = chains;
        }

        @Override
        public void accept(ConnectionContext context, IChainOfResponsibility next) {
           if (index < chains.length) {
             chains[index++].accept(context, this);
           }
        }
    }
}
