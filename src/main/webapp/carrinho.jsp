<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
    <% request.setAttribute("includedHeader", Boolean.TRUE); %> 
    <%@ include
    file="templates/common/header.jsp" %>
    <main>
      <div class="container">
        <h1>Meu Carrinho</h1>
        <span>2 itens</span>
        <section class="cart-section">
                <div class="main-content">
                    <div class="container">
                        <section class="cart-details-page">
                            <ul class="all-cart-items">
                                <li class="cart-item">
                                    <div class="cart-item-container">
                                        <img src="https://placehold.co/98x96" alt="Vinho 2" />
                                        <div class="item-info">
                                            <h2>Vinho Branco Seco</h2>
                                            <span class="description">Mendoza, Argentina</span>
                                            <span class="price">R$ 79,90</span>
                                        </div>
                                        <div class="quantity"><button>-</button><input type="number" value="1" min="1" max="10" /><button>+</button></div>
                                    </div>
                                </li>
                                <li class="cart-item">
                                    <div class="cart-item-container">
                                        <img src="https://placehold.co/98x96" alt="Vinho 2" />
                                        <div class="item-info">
                                            <h2>Vinho Tinto Suave</h2>
                                            <span class="description">Mendoza, Argentina</span>
                                            <span class="price">R$ 79,90</span>
                                        </div>
                                        <div class="quantity"><button>-</button><input type="number" value="1" min="1" max="10"/><button>+</button></div>
                                    </div>
                                </li>
                            </ul>

                            <div class="embalagem">
                                <p>🎁  Embalagem premium (presente)</p>
                                <label><input type="checkbox" /></label>
                            </div>
                            <div class="entrega">
                                <p>📦  Entrega: Segunda, 17 de Março</p>                            
                            </div>
                        </section>
                    </div>
                </div>
                <div class="sidebar">
                    <h3>Resumo do Pedido</h3>
                    <ul>
                        <li><span>Subtotal</span><span>R$ 159,80</span></li>
                        <li><span>Frete</span><span>R$ 15,00</span></li>
                        <li><span>Embalagem Premium</span><span>R$ 15,00</span></li>
                        <li class="total"><span>Total</span><span>R$ 189,80</span></li>
                        <span class="payment-methods">PIX · Cartão · Parcelamento até 6x</span>
                        <button class="checkout-btn">Finalizar Compra</button>
                    </ul>
                </div>
                
                
            </div>
        </section>
      </div>
      <%@ include file="templates/common/footer.jsp" %>
    </main>

    <script>
      document.querySelectorAll('.cart-item').forEach(item => {
        const buttons = item.querySelectorAll('button');
        const input = item.querySelector('input[type="number"]');
        const quantityText = item.querySelector('.item-info p:last-of-type');

        if (buttons.length === 2 && input) {
          const decreaseBtn = buttons[0]; // botão -
          const increaseBtn = buttons[1]; // botão +

          // Função para atualizar o texto de quantidade
          const updateQuantityText = () => {
            quantityText.textContent = `Quantidade: ${input.value}`;
          };

          // Botão de diminuir
          decreaseBtn.addEventListener('click', () => {
            if (parseInt(input.value) > 1) {
              input.value = parseInt(input.value) - 1;
              updateQuantityText();
            }
          });

          // Botão de aumentar
          increaseBtn.addEventListener('click', () => {
            input.value = parseInt(input.value) + 1;
            updateQuantityText();
          });

          // Atualizar ao mudar o input diretamente
          input.addEventListener('change', updateQuantityText);
        }
      });
    </script>
  </body>
</html>
