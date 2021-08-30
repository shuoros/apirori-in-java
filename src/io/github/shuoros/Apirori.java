package io.github.shuoros;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Apirori {

	private static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		System.out.println("1. randomData    2. inputData");
		int menu = Integer.parseInt(sc.nextLine());
		Apirori apirori = new Apirori(menu);
		apirori.run();
	}

	public class Item {
		private List<Integer> itemset;
		private double support;

		public Item() {
			this.itemset = new ArrayList<>();
			this.support = 0.0;
		}

		public Item(List<Integer> itemset) {
			this.itemset = itemset;
			this.support = 0.0;
		}

		public Item copy() {
			Item item = new Item();
			for (Integer i : this.itemset) {
				item.addItemset(i);
			}
			item.setSupport(this.support);
			return item;
		}

		public List<Integer> getItemset() {
			return itemset;
		}

		public void setItemset(List<Integer> itemset) {
			this.itemset = itemset;
		}

		public void addItemset(int item) {
			this.itemset.add(item);
		}

		public double getSupport() {
			return support;
		}

		public void setSupport(double support) {
			this.support = support;
		}

		public int size() {
			return this.itemset.size();
		}

	}

	/** the list of current itemsets */
	private List<Integer> itemsets;

	/** the list of current transactions */
	private List<Item> transactions;

	private HashMap<List<Integer>, Double> supports;

	/** minimum support for a frequent itemset in percentage, e.g. 0.8 */
	private double minSup;

	/** minimum support for a frequent itemset in percentage, e.g. 0.8 */
	private double minConf;

	public Apirori() {
		this(1);
	}

	public Apirori(int menu) {
		itemsets = new ArrayList<>();
		transactions = new ArrayList<>();
		supports = new HashMap<>();
		Random rand = new Random();
		switch (menu) {
		case 1:
			System.out.println("How many items?");
			int itemsCount = Integer.parseInt(sc.nextLine());
			System.out.print("Generated itemset: {");
			for (int i = 0; i < itemsCount; i++) {
				int randomNum;
				do {
					randomNum = rand.nextInt((itemsCount * 2) + 1);
				} while (this.itemsets.contains(randomNum));
				this.itemsets.add(randomNum);
				if (i == itemsCount - 1) {
					System.out.println(randomNum + "}");
				} else {
					System.out.print(randomNum + ", ");
				}
			}
			System.out.println("How many transactions?");
			int transactionsCount = Integer.parseInt(sc.nextLine());
			System.out.println("Min and Max of transactions size?(for example: 1-10)");
			String[] minMax = sc.nextLine().split("-");
			System.out.println("Generated transactions:");
			for (int i = 0; i < transactionsCount; i++) {
				int randomNum = rand.nextInt((Integer.parseInt(minMax[1]) - Integer.parseInt(minMax[0])) + 1)
						+ Integer.parseInt(minMax[0]);
				List<Integer> itemset = new ArrayList<>();
				System.out.print("T" + (i + 1) + ": {");
				for (int j = 0; j < randomNum; j++) {
					int randomItem;
					do {
						randomItem = rand.nextInt((itemsCount));
					} while (itemset.contains(this.itemsets.get(randomItem)));
					itemset.add(this.itemsets.get(randomItem));
					if (j == randomNum - 1) {
						System.out.println(this.itemsets.get(randomItem) + "}");
					} else {
						System.out.print(this.itemsets.get(randomItem) + ", ");
					}
				}
				this.transactions.add(new Item(itemset));
			}
			System.out.println("Minimum support?(in percentage, e.g. 0.8)");
			this.minSup = Double.parseDouble(sc.nextLine());
			System.out.println("Minimum confidence?(in percentage, e.g. 0.8)");
			this.minConf = Double.parseDouble(sc.nextLine());
			break;
		case 2:
			System.out.println("How many items?");
			int itemsCount2 = Integer.parseInt(sc.nextLine());
			System.out.println("Enter " + itemsCount2 + " numbers in each line:");
			for (int i = 0; i < itemsCount2; i++) {
				int item;
				do {
					System.out.print((i + 1) + ": ");
					item = Integer.parseInt(sc.nextLine());
				} while (this.itemsets.contains(item));
				this.itemsets.add(item);
			}
			System.out.println("How many transactions?");
			int transactionsCount2 = Integer.parseInt(sc.nextLine());
			List<Integer> itemset;
			System.out.println("Enter " + transactionsCount2 + " numbers in each line e.g. {1, 2, 3, 4}:");
			for (int i = 0; i < transactionsCount2; i++) {
				System.out.print("T" + (i + 1) + ": ");
				String input = sc.nextLine();
				String[] transaction = input.replaceAll("\\s", "").replaceAll("\\{", "").replaceAll("\\}", "")
						.split(",");
				itemset = new ArrayList<>();
				for (int j = 0; j < transaction.length; j++) {
					itemset.add(Integer.parseInt(transaction[j]));
				}
				this.transactions.add(new Item(itemset));
			}
			System.out.println("Minimum support?(in percentage, e.g. 0.8)");
			this.minSup = Double.parseDouble(sc.nextLine());
			System.out.println("Minimum confidence?(in percentage, e.g. 0.8)");
			this.minConf = Double.parseDouble(sc.nextLine());
			break;
		}

	}

	public void run() {
		System.out.println("===== Start =====");
		int iteration = 1;
		List<Item> lastIteration = null;
		List<Item> currentIteration;
		List<Item> firstIteration = null;
		while (true) {
			currentIteration = new ArrayList<>();
			if (iteration == 1) {
				for (int i = 0; i < this.itemsets.size(); i++) {
					int visit = 0;
					for (int j = 0; j < this.transactions.size(); j++) {
						if (this.transactions.get(j).getItemset().contains(this.itemsets.get(i))) {
							visit++;
						}
					}
					double support = (double) visit / (double) this.transactions.size();
					if (support >= this.minSup) {
						Item item = new Item();
						item.addItemset(this.itemsets.get(i));
						item.setSupport(support);
						currentIteration.add(item);
						this.supports.put(item.getItemset(), support);
					}
				}
				firstIteration = currentIteration;
			} else {
				for (Item items : lastIteration) {
					for (int i = 0; i < firstIteration.size(); i++) {
						if (!items.getItemset().contains(firstIteration.get(i).getItemset().get(0))) {
							Item item = items.copy();
							item.addItemset(firstIteration.get(i).getItemset().get(0));
							int visit = 0;
							for (int j = 0; j < this.transactions.size(); j++) {
								int present = 0;
								for (int k = 0; k < item.getItemset().size(); k++) {
									if (this.transactions.get(j).getItemset().contains(item.getItemset().get(k))) {
										present++;
									}
								}
								if (present == item.getItemset().size()) {
									visit++;
								}
							}
							double support = (double) visit / (double) this.transactions.size();
							if (support >= this.minSup) {
								item.setSupport(support);
								boolean seen = false;
								for (Item currentItems : currentIteration) {
									int visitInCurrentItems = 0;
									for (int j = 0; j < item.getItemset().size(); j++) {
										if (currentItems.getItemset().contains(item.getItemset().get(j))) {
											visitInCurrentItems++;
										}
									}
									if (visitInCurrentItems == item.getItemset().size()) {
										seen = true;
										break;
									}
								}
								if (!seen) {
									currentIteration.add(item);
									this.supports.put(item.getItemset(), support);
								}
							}
						}
					}
				}
			}
			System.out.print("Iteration" + iteration + ": ");
			for (int i = 0; i < currentIteration.size(); i++) {
				System.out.print("{");
				for (int j = 0; j < currentIteration.get(i).getItemset().size(); j++) {
					if (j == currentIteration.get(i).getItemset().size() - 1) {
						System.out.print(currentIteration.get(i).getItemset().get(j) + "} support="
								+ (currentIteration.get(i).getSupport() * 100) + "%");
					} else {
						System.out.print(currentIteration.get(i).getItemset().get(j) + ", ");
					}
				}
				if (i == currentIteration.size() - 1) {
					System.out.println();
				} else {
					System.out.print(" - ");
				}
			}
			if (currentIteration.size() == 1) {
				extratRules(currentIteration);
				break;
			}
			if (currentIteration.size() == 0) {
				extratRules(lastIteration);
				break;
			}
			lastIteration = currentIteration;
			iteration++;
		}
	}

	public void extratRules(List<Item> itemset) {
		System.out.println("===== Result =====");
		if (itemset != null) {
			for (Item item : itemset) {
				List<List<Integer>> subsets = subsets(item);
				for (List<Integer> subset : subsets) {
					List<Integer> value = new ArrayList<>();
					for (Integer i : item.getItemset()) {
						if (!subset.contains(i)) {
							value.add(i);
						}
					}
					double confidence = (double) item.getSupport() / (double) this.supports.get(subset);
					if (confidence >= this.minConf) {
						System.out.print("{");
						for (int i = 0; i < subset.size(); i++) {
							if (i == subset.size() - 1) {
								System.out.print(subset.get(i) + "}");
							} else {
								System.out.print(subset.get(i) + ", ");
							}
						}
						System.out.print(" => {");
						for (int i = 0; i < value.size(); i++) {
							if (i == value.size() - 1) {
								System.out.print(value.get(i) + "}");
							} else {
								System.out.print(value.get(i) + ", ");
							}
						}
						System.out.println();
					}
				}
			}
		} else {
			System.out.println("No rules can be found!");
		}
	}

	/**
	 * https://www.geeksforgeeks.org/finding-all-subsets-of-a-given-set-in-java/
	 * 
	 * @param itemset
	 * @return
	 */
	public List<List<Integer>> subsets(Item item) {
		int size = item.getItemset().size();
		List<List<Integer>> subsets = new ArrayList<>();
		// Run a loop for printing all 2^n
		// subsets one by one
		for (int i = 0; i < (1 << size); i++) {
			List<Integer> subset = new ArrayList<>();
			// Print current subset
			for (int j = 0; j < size; j++) {
				// (1<<j) is a number with jth bit 1
				// so when we 'and' them with the
				// subset number we get which numbers
				// are present in the subset and which
				// are not
				if ((i & (1 << j)) > 0) {
					subset.add(item.getItemset().get(j));
				}
			}
			subsets.add(subset);
		}
		// Omit {}
		subsets.remove(0);
		subsets.remove(subsets.size() - 1);
		return subsets;
	}

}
