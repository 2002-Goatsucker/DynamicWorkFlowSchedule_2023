import networkx as nx
import numpy as np
import matplotlib.pyplot as plt

G = nx.DiGraph()
data1 = np.loadtxt('D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\result_dmga.txt')
G.add_edges_from(data1) 

nx.draw(G, with_labels=True)
plt.show()