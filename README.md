# ProjetoFinalSocket
Atividade final desenvolvida para a matéria de Programação Concorrente e Distribuída.

Segue atividade:
Crie um projeto (em Java 17) para representar um sistema de busca distribuído utilizando sockets. O sistema
deverá ser formado por três servidores (A, B e C). Dois dos três servidores serão responsáveis por uma
busca em metade de um dado de artigos científicos do arXiv
(https://paperswithcode.com/dataset/arxiv-10).
Um dos servidores (A) deverá receber a solicitação de busca do cliente, contendo uma substring de um
possível título ou introdução de um artigo, enviar ao outros dois servidores (B e C) e em seguida ambos
devem realizar a busca no dado designado a cada um.
Após a busca, o servidor A deve enviar ao cliente o resultado da busca.
Observações:
● Os arquivos JSON com os dados será divulgado previamente pelo professor;
● A comunicação entre cliente e servidor deve ser feita obrigatoriamente por meio de sockets;
● O algoritmo de busca em cada servidor deverá ser escolhido, justificado e explicado pelo grupo. A
string enviada pelo cliente deve ser buscada como uma substring, ou seja, amostras que
contenham a string enviada no título ou na introdução devem ser retornados. Algumas opções
de algoritmos são listadas aqui: https://www.geeksforgeeks.org/pattern-searching/

