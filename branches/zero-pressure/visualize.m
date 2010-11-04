results = load('results.txt');
results(:,1) = results(:,1) - results(1,1);
%plot(results(:,1),results(:,4))
plot(results(:,3),results(:,2));