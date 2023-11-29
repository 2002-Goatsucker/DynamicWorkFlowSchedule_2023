import numpy as np
import matplotlib.pyplot as plt

if __name__ == "__main__":
    plt.figure(0);
    x = [i + 1 for i in range(500)]
    data1 = np.loadtxt("C:\\Users\\徐璟源\\Desktop\\git repository\\DynamicScheduling_2023\\src\\main\\resources\\result\\hv_nsgaiib.txt")
    data2 = np.loadtxt("C:\\Users\\徐璟源\\Desktop\\git repository\\DynamicScheduling_2023\\src\\main\\resources\\result\\hv_fogmp.txt")
    data3 = np.loadtxt("C:\\Users\\徐璟源\\Desktop\\git repository\\DynamicScheduling_2023\\src\\main\\resources\\result\\hv_idg.txt")
    data4 = np.loadtxt("C:\\Users\\徐璟源\\Desktop\\git repository\\DynamicScheduling_2023\\src\\main\\resources\\result\\hv_mb.txt")
    data5 = np.loadtxt("C:\\Users\\徐璟源\\Desktop\\git repository\\DynamicScheduling_2023\\src\\main\\resources\\result\\hv_dmga.txt")

    plt.plot(x, data1)
    plt.plot(x, data2)
    plt.plot(x, data3)
    plt.plot(x, data4)
    plt.plot(x, data5)
    plt.legend(['DNSGAIIB', 'FOGMP', 'DNSGAIIgIDG', 'MBNSGAII','DMGA'])
    plt.title("Inspiral_100 80ins 0.08severity")
    plt.savefig("C:\\Users\\徐璟源\\Desktop\\git repository\\DynamicScheduling_2023\\src\\main\\resources\\result\\fig.png")
    plt.show()